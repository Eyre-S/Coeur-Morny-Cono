package cc.sukazyo.cono.morny.core.module

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.MornyCoeur.*

trait MornyModule {
	
	/** The unique id of this Morny module.
	  *
	  * Requires to be unique between modules that will load, requires only contains ASCII
	  * characters excepts control characters.
	  *
	  * This module id will list in the module lists as the primary key. Although currently
	  * is no checks to make sure this is unique (and if is legal), it is still highly
	  * recommended making this unique between modules, for forward compatibles and third-party
	  * compatibles.
	  *
	  * Recommended to be named using all lowercased characters and hyphen character (`-`) like
	  * `module-author-or-provider.module-name` . For example, the Morny internal module
	  * `morny_misc` has the id `morny.misc`, and `stickers_get` has the id `morny.stickers-get`.
	  */
	val id: String
	/** Human-readable name of this Morny module.
	  *
	  * No specific formats requires except do not contain ASCII control characters.
	  *
	  * No special usage except showing in module table.
	  *
	  * Recommended set this to an English name while the i18n for this is not implemented yet.
	  */
	val name: String
	/** Version of this module build.
	  *
	  * No special usage except showing in module table.
	  */
	val version: String
	
	/** Human-readable description of this Morny module.
	  *
	  * Should be a Markdown document.
	  *
	  * No special usage.
	  *
	  * Recommended to use English while the i18n for this is not implemented yet.
	  */
	val description: String | Null
	
	/** The others module's feature that this module provides.
	  *
	  * Here should be the modules IDs. Means that this module is a full replacement of the
	  * listed modules.
	  *
	  * It can also provide the feature IDs, makes a better dependency management about feature
	  * id instead of module id.
	  *
	  * Two or more modules cannot have the same module ID, event provides the same module ID.
	  *
	  * @since 2.0.0-alpha21
	  */
	val provide: Seq[String] = Nil
	
	/** Which modules that this module requires.
	  *
	  * Here should be the modules IDs, indicates a relation of this module and the listed
	  * modules that this module requires the listed modules.
	  *
	  * If the required modules does not exist, the module loader will reject completing
	  * loading modules and throw errors.
	  *
	  * It does not define any loading order about the modules, just check if the required
	  * module exists. Combine [[after]] or [[before]] to define the loading order, or use
	  * [[depends]] which will also define ordering.
	  *
	  * @since 2.0.0-alpha21
	  */
	val requires: Seq[String] = Nil
	/** Which modules that this module depends on.
	  *
	  * Here should be the modules IDs, indicates a relation of this module and the listed
	  * modules that this module depends on the listed modules.
	  *
	  * If the depended on modules does not exist, the module loader will reject completing
	  * loading modules and throw errors.
	  *
	  * This module will be loaded after the depended on modules.
	  *
	  * It is a combine of [[after]] and [[requires]].
	  *
	  * @since 2.0.0-alpha21
	  */
	val depends: Seq[String] = Nil
	/** Defines this module should be loaded after the listed modules.
	  *
	  * Here should be the module IDs.
	  *
	  * Notice that if the listed module does not exist, it will be ignored but not throw
	  * errors. Combine [[requires]] to define the requirement relation, or use [[depends]]
	  * instead.
	  *
	  * @since 2.0.0-alpha21
	  */
	val after: Seq[String] = Nil
	/** Defines this module should be loaded before the listed modules.
	  *
	  * Here should be the module IDs.
	  *
	  * Notice that if the listed module does not exist, it will be ignored but not throw
	  * errors. Combine [[requires]] to define the requirement relation.
	  *
	  * @since 2.0.0-alpha21
	  */
	val before: Seq[String] = Nil
	
	def onInitializingPre (using MornyCoeur)(cxt: OnInitializingPreContext): Unit = {}
	
	def onInitializing (using MornyCoeur)(cxt: OnInitializingContext): Unit = {}
	
	def onInitializingPost (using MornyCoeur)(cxt: OnInitializingPostContext): Unit = {}
	
	def onStarting (using MornyCoeur)(cxt: OnStartingContext): Unit = {}
	
	def onStartingPost (using MornyCoeur)(cxt: OnStartingPostContext): Unit = {}
	
	def onRoutineSavingData (using MornyCoeur): Unit = {}
	
	def onExiting (using MornyCoeur): Unit = {}
	
	def onExitingPost (using MornyCoeur): Unit = {}
	
	def onExited (using MornyCoeur): Unit = {}
	
}
