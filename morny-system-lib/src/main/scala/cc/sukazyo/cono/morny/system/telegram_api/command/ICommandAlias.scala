package cc.sukazyo.cono.morny.system.telegram_api.command

/** One alias definition, contains the necessary message of how
  * to process the alias.
  */
trait ICommandAlias {
	
	/** The alias name.
	  *
	  * same with the command name, it is the unique identifier of this alias.
	  */
	val name: String
	/** If the alias should be listed while list commands to end-user.
	  *
	  * The alias can only be listed when the parent command can be listed
	  * (meanwhile the parent command implemented [[ITelegramCommand]]). If the
	  * parent command cannot be listed, it will always cannot be listed.
	   */
	val listed: Boolean
	
}

/** Default implementations of [[ICommandAlias]]. */
object ICommandAlias {
	
	/** Alias which can be listed to end-user.
	  *
	  * the [[ICommandAlias.listed]] value is always true.
	  *
	  * @param name The alias name, see more in [[ICommandAlias.name]]
	  */
	case class ListedAlias (name: String) extends ICommandAlias:
		override val listed = true
	
	/** Alias which cannot be listed to end-user.
	  * 
	  * the [[ICommandAlias.listed]] value is always false.
	  * 
	  * @param name The alias name, see more in [[ICommandAlias.name]]
	  */
	case class HiddenAlias (name: String) extends ICommandAlias:
		override val listed = false
	
}
