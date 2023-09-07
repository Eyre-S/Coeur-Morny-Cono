package cc.sukazyo.cono.morny.bot.command

trait ICommandAlias {
	
	val name: String
	val listed: Boolean
	
}

object ICommandAlias {
	
	case class ListedAlias (name: String) extends ICommandAlias:
		override val listed = true
	
	case class HiddenAlias (name: String) extends ICommandAlias:
		override val listed = false
	
}
