package cc.sukazyo.cono.morny.bot.command

/** A complex telegram command.
  * 
  * the extension of [[ISimpleCommand]], with external defines of the necessary
  * introduction message ([[paramRule]] and [[description]]).
  * 
  * It can be listed to end-user.
  */
trait ITelegramCommand extends ISimpleCommand {
	
	/** The param rule of this command, used in human-readable command list.
	  * 
	  * The param rule uses a symbol language to describe how this command
	  * receives paras.
	  * 
	  * Set it empty to make this scope not available.
	  */
	val paramRule: String
	/** The description/introduction of this command, used in human-readable
	  * command list.
	  */
	val description: String
	
}
