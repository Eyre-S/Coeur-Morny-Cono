package cc.sukazyo.cono.morny.core

import cc.sukazyo.cono.morny.core.MornyCoeur.*

trait MornyModule {
	
	/** The unique id of this Morny module.
	  *
	  * Requires to be unique between modules that will loaded, requires only contains ASCII
	  * characters excepts control characters.
	  *
	  * This module id will listed in the module lists as the primary key. Although currently
	  * is no checks to make sure this is unique (and if is legal), it is still highly
	  * recommended making this unique between modules, for forward compatibles and third-party
	  * compatibles.
	  *
	  * Recommended to be named using all lowercased characters and hyphen character (`-`) like
	  * `module-author-or-provider.module-name` . For example, the Morny internal module
	  * `morny_misc` has the id `morny.misc`, and `stickers_get` has the id `morny.stickers-get`.
	  */
	val id: String
	/** Human readable name of this Morny module.
	  *
	  * No specific formats requires except do not contains ASCII control characters.
	  *
	  * No special usage except showing in module table.
	  *
	  * Recommended set this to a English name while the i18n for this is not implemented yet.
	  */
	val name: String
	/** Version of this module build.
	  *
	  * No special usage except showing in module table.
	  */
	val version: String
	
	/** Human readable description of this Morny module.
	  *
	  * Should be a Markdown document.
	  *
	  * No special usage.
	  *
	  * Recommended to use English while the i18n for this is not implemented yet.
	  */
	val description: String | Null
	
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
