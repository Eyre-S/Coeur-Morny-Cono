package cc.sukazyo.cono.morny.core.assets

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.assets.MornyAssets.{AssetPackConflictException, AssetPackLoadException, METADATA_FILE, NotAssetPackException}
import cc.sukazyo.restools.{ResourceDirectory, ResourceFile}
import cc.sukazyo.restools.utils.PathsHelper
import io.circe.{DecodingFailure, ParsingFailure}

import java.io.IOException
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.boundary
import scala.util.boundary.break

object MornyAssets {
	
	val METADATA_FILE = "morny-assets.jsonc"
	
	def inCoeur (using coeur: MornyCoeur): MornyAssets =
		coeur.assets
	
	class NotAssetPackException (val resourceDirectory: ResourceDirectory)
	extends Exception(s"Directory is not an asset pack, due to metadata file ($METADATA_FILE) is not found.\n" +
		s"  directory: ${PathsHelper.compile(resourceDirectory.getPath)} under pack ${resourceDirectory.getOwnerPackage}")
	
	class AssetPackLoadException (val resourceDirectory: ResourceDirectory)
	extends Exception(s"Failed to load asset pack's metadata:\n" +
		s"  directory: ${PathsHelper.compile(resourceDirectory.getPath)} under pack ${resourceDirectory.getOwnerPackage}")
	
	class AssetPackConflictException (val existingPack: MornyAssets#AssetPack, val newOne: ResourceDirectory)
	extends Exception(s"Asset pack with id '${existingPack.metadata.id}' have already registered!")
	
}

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
	
	def getFile (path: String): ResourceFile = this.getFile(PathsHelper.parseString(path)*)
	def getFile (path: String*): ResourceFile = boundary[ResourceFile] {
		for (dir <- assetDirs) {
			val file = dir._2.getFile(path*)
			if file != null then break(file)
		}
		throw IllegalArgumentException(s"Asset file ${PathsHelper.compile(path.toArray)} cannot be found!")
	}
	
	def getFileInGroup (group: String, path: String): ResourceFile =
		this.getFileInGroup(group, PathsHelper.parseString(path)*)
	def getFileInGroup (group: String, path: String*): ResourceFile = boundary[ResourceFile] {
		for (dir <- assetDirs) {
			if (dir._1 == group)
				val file = dir._2.getFile(path*)
				if file != null then break(file)
		}
		throw IllegalArgumentException(s"Asset file ${PathsHelper.compile(path.toArray)} cannot be found!")
	}
	
}
