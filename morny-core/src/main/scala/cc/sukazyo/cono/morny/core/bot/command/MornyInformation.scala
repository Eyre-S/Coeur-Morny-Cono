package cc.sukazyo.cono.morny.core.bot.command

import cc.sukazyo.cono.morny.core.{MornyCoeur, MornySystem}
import cc.sukazyo.cono.morny.core.bot.api.messages.{ErrorMessage, MessagingContext}
import cc.sukazyo.cono.morny.core.bot.api.SimpleCommandManager
import cc.sukazyo.cono.morny.data.MornyInformation.*
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramParseEscape.escapeHtml as h
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Requests.unsafeExecute
import cc.sukazyo.cono.morny.system.telegram_api.command.{ICommandAlias, InputCommand, ISimpleCommand, ITelegramCommand}
import cc.sukazyo.cono.morny.system.telegram_api.command.ICommandAlias.HiddenAlias
import cc.sukazyo.cono.morny.util.CommonFormat.{formatDate, formatDuration}
import cc.sukazyo.cono.morny.util.var_text
import cc.sukazyo.cono.morny.util.var_text.VarText
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{SendMessage, SendPhoto, SendSticker}

import java.lang.System

class MornyInformation (using coeur: MornyCoeur) extends SimpleCommandManager with ITelegramCommand  {
	import coeur.dsl.{*, given}
	
	override val name: String = "info"
	override val aliases: List[ICommandAlias] = Nil
	override val paramRule: String = "[(version|runtime|stickers[.IDs]|tasks|event)]"
	override val description: String = "输出当前 Morny 的各种信息"
	
	this.register(Info, Version, _Runtime, TasksStatus)
	private[core] val toplevelCommands: List[ISimpleCommand] = List(Version_Toplevel, _Runtime)
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		command.args.headOption match
			// due to the complex of stickers.XXX subcommands
			//  matches all the commands that starts with "stickers"
			case Some(s) if s.startsWith(Stickers.name) =>
				Stickers.execute
			case _ =>
		
		this.emitCommands(using command.subcommand, event)
		
	}
	
	override protected def nonCommandExecutable (using command: InputCommand, event: Update): Boolean =
		echo404
		true
	
	private case object Info extends ISimpleCommand:
		override val name: String = ""
		override val aliases: List[ICommandAlias] = Nil
		override def execute (using command: InputCommand, event: Update): Unit =
			echoInfo(event)
	private def echoInfo (update: Update): Unit = {
		val cxt = MessagingContext.extract(using update.message)
		val cxtReplied = Option(update.message.replyToMessage).map(MessagingContext.extract(using _))
		
		cxtReplied match
			case None =>
			case Some(_cxtReplied) =>
				// check if theres associated information about error message on the replied context.
				//   if there really is, that means the replied message is a error message,
                //   so the error message's complex information will be sent
				coeur.errorMessageManager.inspectMessage(_cxtReplied.toChatMessageKey) match
					case None =>
					case Some(errMessage) =>
						coeur.errorMessageManager.sendErrorMessage(errMessage, ErrorMessage.Types.Complex, Some(cxt))
		
		// if theres no any associated information on the context
		SendPhoto(
			cxt.bind_chat.id,
			getAboutPic
		).caption(
			translations.trans(
				"morny.command.info.message.about",
				translations.transAsVar("morny.information.about_links", getMornyAboutLinksVars*)(using cxt.bind_message.from.prefer_language)
			)(using cxt.bind_message.from.prefer_language)
		).parseMode(ParseMode HTML).replyToMessageId(cxt.bind_message.messageId)
			.unsafeExecute
		
	}
	
	private case object Stickers extends ISimpleCommand {
		
		override val name: String = "stickers"
		override val aliases: List[ICommandAlias] = Nil
		
		/** Process the parameter, to find the sticker name hidden in params.
		  *
		  * Supports:
		  *  - args is `stickers`, send all stickers
		  *  - args is `stickers.xxx`, send the sticker named `xxx`
		  *  - args is `stickers#xxx`, send the sticker named `xxx`
		  *  - args is `stickers` `xxx`, send the sticker named `xxx`
		  *  - args is `xxx`, send the sticker named `xxx`
		  */
		override def execute (using command: InputCommand, event: Update): Unit = {
			val mid: String | Null =
				if (command.args(0) == this.name) {
					if (command.args.length == 1) ""
					else if (command.args.length == 2) command.args(1)
					else null
				} else if (command.args.length == 1) {
					if ((command.args(0) `startsWith` s"${this.name}.") || (command.args(0) `startsWith` s"${this.name}#")) {
						command.args(0) `substring` this.name.length + 1
					} else command.args(0)
				} else null
			if (mid == null) echo404
			else echoStickers(mid)(using event.message.chat.id, event.message.messageId)
		}
		
		/** Send the sticker named mid, or send all stickers if mid is not set.
		  *
		  * - If mid is set and is a sticker name, send the specific one sticker.
		  * - If mid is set and is not a sticker name, send 404.
		  * - If mid is not set, send all stickers that available in [[TelegramStickers]].
		  */
		private def echoStickers (mid: String)(using send_chat: Long, send_replyTo: Int)(using Update): Unit = {
			import scala.jdk.CollectionConverters.*
			if (mid isEmpty) for ((_key, _file_id) <- TelegramStickers.map asScala)
				sent(_key, _file_id)
			else {
				try {
					val sticker = TelegramStickers `getById` mid
					sent(sticker.getKey, sticker.getValue)
				} catch case _: NoSuchFieldException => {
					echo404
				}
			}
		}
		
		/** Send the specific sticker to telegram. */
		private def sent (mid: String, file_id: String)(using send_chat: Long, send_replyTo: Int): Unit = {
			val send_mid = SendMessage(send_chat, mid)
			val send_sticker = SendSticker(send_chat, file_id)
			if (send_replyTo != -1) send_mid.replyToMessageId(send_replyTo)
			val result_send_mid = send_mid.unsafeExecute
			send_sticker.replyToMessageId(result_send_mid.message.messageId)
			send_sticker.unsafeExecute
		}
		
	}
	
	case object Version extends ISimpleCommand:
		override val name: String = "version"
		override val aliases: List[ICommandAlias] = List(HiddenAlias("v"))
		override def execute (using command: InputCommand, event: Update): Unit =
			echoVersion
	private case object Version_Toplevel extends ISimpleCommand:
		override val name: String = "version"
		override val aliases: List[ICommandAlias] = Nil
		override def execute (using command: InputCommand, event: Update): Unit =
			echoVersion
	private[command] def echoVersion (using event: Update): Unit = {
		val versionDeltaHTML = MornySystem.VERSION_DELTA match {case Some(d) => s"-δ<code>${h(d)}</code>" case None => ""}
		val versionGitHTML = if (MornySystem.GIT_COMMIT nonEmpty) s"git $getVersionGitTagHTML" else ""
		SendMessage(
			event.message.chat.id,
			VarText(
				// language=html
				"""version:
				  |- Morny <code>{version_codename}</code>
				  |- <code>{version_base}</code>{version_delta_html}{version_git_suffix}
				  |coeur md5_hash:
				  |- <code>{md5}</code>
				  |coding timestamp:
				  |- <code>{time_millis}</code>
				  |- <code>{time_utc} [UTC]</code>
				  |""".stripMargin
			).render(
				"version_codename" -> h(MornySystem.CODENAME.toUpperCase),
				"version_base" -> h(MornySystem.VERSION_BASE),
				"version_delta_html" -> versionDeltaHTML,
				"version_git_suffix" -> (if (MornySystem.GIT_COMMIT nonEmpty) "\n- " + versionGitHTML else ""),
				"md5" -> h(MornySystem.getJarMD5),
				"time_millis" -> MornySystem.CODE_TIMESTAMP,
				"time_utc" -> h(formatDate(MornySystem.CODE_TIMESTAMP, 0)),
			)
		).replyToMessageId(event.message.messageId).parseMode(ParseMode HTML)
			.unsafeExecute
	}
	
	private case object _Runtime extends ISimpleCommand:
		override val name: String = "runtime"
		override val aliases: List[ICommandAlias] = Nil
		override def execute (using command: InputCommand, event: Update): Unit =
			echoRuntime
	private[command] def echoRuntime (using event: Update): Unit = {
		def sysprop (p: String): String = System.getProperty(p)
		SendMessage(
			event.message.chat.id,
			VarText(
				/* language=html */
				"""system:
				  |- <code>{hostname}</code>
				  |- <code>{os.name}</code> <code>{os.arch}</code> <code>{os.version}</code>
				  |java runtime:
				  |- <code>{java.vm.vendor}.{java.vm.name}</code>
				  |- <code>{java.vm.version}</code>
				  |vm memory:
				  |- <code>{memory_used_mb}</code> / <code>{memory_available_mb}</code> MB
				  |- <code>{cpu_cores}</code> cores
				  |coeur version:
				  |- {version_full}
				  |- <code>{coeur_md5}</code>
				  |- <code>{compile_time_utc} [UTC]</code>
				  |- [<code>{compile_time_millis}</code>]
				  |continuous:
				  |- <code>{running_duration}</code>
				  |- [<code>{running_duration_ms}</code>]
				  |- <code>{startup_time_utc}</code>
				  |- [<code>{startup_time_millis}</code>]"""
					.stripMargin
			).render(
				"hostname" -> h(if getRuntimeHostname nonEmpty then getRuntimeHostname.get else "<unknown-host>"),
				"os.name" -> h(sysprop("os.name")),
				"os.arch" -> h(sysprop("os.arch")),
				"os.version" -> h(sysprop("os.version")),
				"java.vm.vendor" -> h(sysprop("java.vm.vendor")),
				"java.vm.name" -> h(sysprop("java.vm.name")),
				"java.vm.version" -> h(sysprop("java.vm.version")),
				"memory_used_mb" -> (Runtime.getRuntime.totalMemory/1024/1024),
				"memory_available_mb" -> (Runtime.getRuntime.maxMemory/1024/1024),
				"cpu_cores" -> Runtime.getRuntime.availableProcessors,
				"version_full" -> getVersionAllFullTagHTML,
				"coeur_md5" -> h(MornySystem.getJarMD5),
				"compile_time_utc" -> h(formatDate(MornySystem.CODE_TIMESTAMP, 0)),
				"compile_time_millis" -> MornySystem.CODE_TIMESTAMP,
				"running_duration" -> h(formatDuration(System.currentTimeMillis - coeur.coeurStartTimestamp)),
				"running_duration_ms" -> (System.currentTimeMillis - coeur.coeurStartTimestamp),
				"startup_time_utc" -> h(formatDate(coeur.coeurStartTimestamp, 0)),
				"startup_time_millis" -> coeur.coeurStartTimestamp
			)
		).parseMode(ParseMode HTML).replyToMessageId(event.message.messageId)
			.unsafeExecute
	}
	
	private case object TasksStatus extends ISimpleCommand:
		override val name: String = "tasks"
		override val aliases: List[ICommandAlias] = Nil
		override def execute (using command: InputCommand, event: Update): Unit =
			echoTasksStatus
	private def echoTasksStatus (using update: Update): Unit = {
//		if !coeur.trusted.isTrusted(update.message.from.id) then return;
		SendMessage(
			update.message.chat.id,
			VarText(
				// language=html
				"""<b>Coeur Task Scheduler:</b>
				  | - <i>scheduled tasks</i>: <code>{coeur.tasks.amount}</code>
				  | - <i>scheduler status</i>: <code>{coeur.tasks.state}</code>
				  | - <i>current runner status</i>: <code>{coeur.tasks.runnerState}</code>
				  |""".stripMargin
			).render(
				"coeur.tasks.amount" -> coeur.tasks.amount,
				"coeur.tasks.state" -> coeur.tasks.state,
				"coeur.tasks.runnerState" -> coeur.tasks.runnerState
			)
		).parseMode(ParseMode.HTML).replyToMessageId(update.message.messageId)
			.unsafeExecute
	}
	
	private def echo404 (using event: Update): Unit =
		SendSticker(
			event.message.chat.id,
			TelegramStickers ID_404
		).replyToMessageId(event.message.messageId)
			.unsafeExecute
	
}

object MornyInformation {
	
	def inCoeur (using coeur: MornyCoeur): MornyInformation =
		coeur.$MornyInformation
	
}
