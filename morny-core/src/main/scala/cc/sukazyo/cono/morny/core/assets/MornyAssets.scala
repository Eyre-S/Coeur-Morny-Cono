package cc.sukazyo.cono.morny.core.assets

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.assets.MornyAssets.{AssetPackConflictException, AssetPackLoadException, METADATA_FILE, NotAssetPackException}
import cc.sukazyo.restools.utils.PathsHelper
import cc.sukazyo.restools.{ResourceDirectory, ResourceFile}
import io.circe.{DecodingFailure, ParsingFailure}

import java.io.IOException
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.boundary
import scala.util.boundary.break

/** @see [[MornyAssets!]] */
object MornyAssets {
	
	/** Filename/path of the [[AssetPackMetadata assets metadata file]].
	  *
	  * This file path is relavant to `<assets-root>`. Different loading method may have
	  * different assets-root.
	  *
	  * @see [[AssetPackLoader]] More information about assets-root.
	  */
	val METADATA_FILE = "morny-assets.jsonc"
	
	/** Get the global [[MornyAssets]] instance in current Morny Coeur.
	  */
	def inCoeur (using coeur: MornyCoeur): MornyAssets =
		coeur.assets
	
	/** This [[resourceDirectory]] is not an asset pack.
	  *
	  * Possibly because the [[METADATA_FILE]] is not found in this directory.
	  *
	  * @param resourceDirectory The directory that assets pack loader is trying to load.
	  */
	class NotAssetPackException (val resourceDirectory: ResourceDirectory)
	extends Exception(s"Directory is not an asset pack, due to metadata file ($METADATA_FILE) is not found.\n" +
		s"  directory: ${PathsHelper.compile(resourceDirectory.getPath)} under pack ${resourceDirectory.getOwnerPackage}")
	
	/** Failed to load asset pack in [[resourceDirectory given location]].
	  *
	  * Common errors, see the cause for details.
	  *
	  * @param resourceDirectory The directory that assets pack loader is trying to load.
	  */
	class AssetPackLoadException (val resourceDirectory: ResourceDirectory)
	extends Exception(s"Failed to load asset pack's metadata:\n" +
		s"  directory: ${PathsHelper.compile(resourceDirectory.getPath)} under pack ${resourceDirectory.getOwnerPackage}")
	
	/** When registering [[newOne an assets pack]] to [[MornyAssets assets (pack) manager]],
	  * [[existingPack another pack]] that have already registered have the same
	  * [[AssetPackMetadata.id id]] with the new one.
	  *
	  * Assets pack id must be unique, so this is a conflict.
	  *
	  * @param existingPack The asset pack that have already registered with the same
	  *                     [[AssetPackMetadata.id id]] as the new one.
	  * @param newOne A [[ResourceDirectory]] that is trying to be registered as an asset pack,
	  *               but have the same [[AssetPackMetadata.id id]] with [[existingPack]].
	  */
	class AssetPackConflictException (val existingPack: MornyAssets#AssetPack, val newOne: ResourceDirectory)
	extends Exception(s"Asset pack with id '${existingPack.metadata.id}' have already registered!")
	
}

/** Assets (resource files) manager for Morny.
  *
  * Assets are organized in packs. Packs can contain one or more namespace, and real assets are
  * stored in these namespaces.
  *
  * ## Getting MornyAssets instance
  *
  * Every Coeur instance has a global MornyAssets instance. You can get it by using
  * [[MornyAssets$.inCoeur]] method (when you have a MornyCoeur instance implicitly provided in
  * current scope).
  *
  * ## Assets pack
  *
  * Assets pack is where the assets store. Typically, every module that have its own assets
  * has one assets pack. Assets packs may also be loaded from external sources as the override
  * or expansion for defaults assets.
  *
  * To register an assets pack to MornyAssets, use [[register]] method.
  *
  * ### Pack file structure
  *
  * ```
  * (assets-root)
  * - morny-assets.jsonc : Morny Assets Pack metadata description file
  * - <namespace-id>
  *   - <assets-files...>
  * - <namespace-id>
  *   - <assets-files...>
  * - ...
  * ```
  *
  * ### Metadata
  *
  * Every pack must have a [[METADATA_FILE metadata file]], which is `morny-assets.jsonc`. This
  * file contains necessary information or optional detailed information about this asset pack.
  *
  * ### Namespace
  *
  * One pack can contain one or more namespaces. Every real assets / resource files, is stored
  * under one namespace. In most cases, to read a file, you need to specify the file name/path,
  * and the namespace that this file is stored in.
  *
  * One pack can contain multiple namespaces. Multiple packs can also contain the same
  * namespace and has the same file under this namespace, in this case, the pack that has
  * higher priority will be used (overrides the laters).
  *
  * ## Assets packs priority
  *
  * Custom defined priority is not implemented yet.
  *
  * For now, the priority is determined by the loading order. A pack that is loaded/registered
  * earlier will have higher priority than the later one.
  *
  * ## Getting files
  *
  * Use [[get]] methods to get a file. It receives the namespace and the file path, returns
  * a [[ResourceFile]].
  *
  * > Note that if the file does not exist, an exception will be thrown.
  *
  * @example
  *
  * > Note that the example assumes that you have [[MornyCoeur]], since MornyAssets is designed
  * > as a subsystem of that.
  *
  * {{{
  * // find and get file "core/images/logo.png", using `inCoeur` method to get MornyAssets
  * // instance.
  * MornyAssets.inCoeur.get("core", "images/logo.png")
  * // same, but in path array format.
  * MornyAssets.inCoeur.get("core", "images", "logo.png")
  * // same path array format, for someone who don't like varargs.
  * // if you have a path array already, you may prefer this.
  * MornyAssets.inCoeur.get("core", "images":: "logo.png" :: Nil)
  * val path = List("images", "logo.png")
  * MornyAssets.inCoeur.get("core", path)
  * }}}
  *
  * @see [[MornyCoeur!.assets]] : where the global MornyAssets instance is stored in MornyCoeur.
  * @see [[AssetPackMetadata]] : More info about assets pack metadata.
  * @see [[AssetPackLoader]] : More info about how Morny is loading assets packs, also related
  *      with where the assets-root is for different loading method.
  */
class MornyAssets {
	
	class AssetGroup (val name: String, val dir: ResourceDirectory)
	class AssetPack (val metadata: AssetPackMetadata) {
		val groups: ListBuffer[AssetGroup] = ListBuffer.empty
	}
	
	private val packs = mutable.HashMap[String, AssetPack]()
	private def getPackOrCreate (packMetadata: AssetPackMetadata): AssetPack = {
		packs.get(packMetadata.id) match {
			case Some(pack) => pack
			case None =>
				val pack = new AssetPack(packMetadata)
				packs += (packMetadata.id -> pack)
				pack
		}
	}
	
	private def registerAssetPack (packMetadata: AssetPackMetadata)(assetGroup: String, resourceDirectory: ResourceDirectory)
	: MornyAssets.this.type = {
		this.getPackOrCreate(packMetadata).groups += AssetGroup(assetGroup, resourceDirectory)
		logger.debug(s"from asset pack '${packMetadata.id}' loaded group '$assetGroup' ")
		this
	}
	
	/** Register an assets pack to Morny Assets manager.
	  *
	  * This method receives a [[ResourceDirectory]] that is a valid assets pack root directory.
	  *
	  * @param resourceDirectory The assets root directory. This directory must directly
	  *                          contain the [[METADATA_FILE]]. For dirs from classpath, it
	  *                          should be `/assets` dir under specification (when root is
	  *                          classpath root).
	  * @throws NotAssetPackException if the [[METADATA_FILE]] is not found in this directory.
	  * @throws AssetPackLoadException if this [[ResourceDirectory]] cannot be read, or the
	  *                                metadata file cannot be read or parsed.
	  
	  * @throws AssetPackConflictException if the [[AssetPackMetadata.id]] is already
	  *                                    registered by another asset pack.
	  *
	  * @see [[AssetPackLoader]]: Tools that may helps to get/check a proper assets pack
	  *      [[ResourceDirectory]]
	  *
	  * @return This [[MornyAssets]] instance, for chaining.
	  */
	@throws[NotAssetPackException]
	@throws[AssetPackLoadException]
	@throws[AssetPackConflictException]
	def register (resourceDirectory: ResourceDirectory): MornyAssets.this.type = {
		
		val metadata = Option(resourceDirectory.getFile(METADATA_FILE)) match {
			case None =>
				throw NotAssetPackException(resourceDirectory)
			case Some(file) => try {
				AssetPackMetadata.fromJsonText(file.readString)
			} catch
				case e: (IOException | DecodingFailure | ParsingFailure) =>
					throw AssetPackLoadException(resourceDirectory).initCause(e)
		}
		val _register = this.registerAssetPack(metadata)
		logger.debug(s"loaded asset pack '${metadata.id}' from resource pack ${resourceDirectory.getOwnerPackage}")
		
		if packs.contains(metadata.id) then
			throw AssetPackConflictException(packs(metadata.id), resourceDirectory)
		
		for (pack <- resourceDirectory.listDirectories()) {
			val packName = pack.getPath.last
			_register(packName, pack)
		}
		
		this
		
	}
	
	def assetPacks: List[AssetPack] =
		packs.values.toList
	
	def assetDirs: List[(String, ResourceDirectory)] =
		packs.flatMap(_._2.groups).map(i => (i.name, i.dir)).toList
	
	@deprecated("not ready for use")
	def getFile (path: String): ResourceFile = this.getFile(PathsHelper.parseString(path)*)
	@deprecated("not ready for use")
	def getFile (path: String*): ResourceFile = boundary[ResourceFile] {
		for (dir <- assetDirs) {
			val file = dir._2.getFile(path*)
			if file != null then break(file)
		}
		throw IllegalArgumentException(s"Asset file ${PathsHelper.compile(path.toArray)} cannot be found!")
	}
	
	/** Get a file in a specific namespace.
	  *
	  * @param namespace Target namespace.
	  * @param path Target file path. The string formatted path will be parsed by
	  *             [[PathsHelper.parseString]], and the result will be used as path segments.
	  *
	  * @throws IllegalArgumentException if the file cannot be found.
	  *
	  * @return A [[ResourceFile]].
	  */
	@throws[IllegalArgumentException]
	def get (namespace: String, path: String): ResourceFile =
		this.get(namespace, PathsHelper.parseString(path)*)
	
	/** Get a file in a specific namespace.
	  *
	  * @param namespace Target namespace.
	  * @param path      Target file path. The string formatted path will be parsed by
	  *                  [[PathsHelper.parseString]], and the result will be used as path segments.
	  *
	  * @throws IllegalArgumentException if the file cannot be found.
	  *
	  * @return A [[ResourceFile]].
	  */
	@throws[IllegalArgumentException]
	def get (namespace: String, path: String*): ResourceFile = boundary[ResourceFile] {
		for (dir <- assetDirs) {
			if (dir._1 == namespace)
				val file = dir._2.getFile(path*)
				if file != null then break(file)
		}
		throw IllegalArgumentException(s"Asset file \"${PathsHelper.compile(path.toArray)}\" cannot be found!")
	}
	
	/** Get a file in a specific namespace.
	  *
	  * @param namespace Target namespace.
	  * @param path      Target file path. The string formatted path will be parsed by
	  *                  [[PathsHelper.parseString]], and the result will be used as path segments.
	  *
	  * @throws IllegalArgumentException if the file cannot be found.
	  * @return A [[ResourceFile]].
	  */
	@throws[IllegalArgumentException]
	def get (namespace: String, path: List[String]): ResourceFile =
		this.get(namespace, path*)
	
}
