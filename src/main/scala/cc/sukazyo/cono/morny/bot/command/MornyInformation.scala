package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.{BuildConfig, MornyCoeur, MornySystem}
import cc.sukazyo.cono.morny.data.MornyInformation.*
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.CommonFormat.{formatDate, formatDuration}
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{SendMessage, SendPhoto, SendSticker}

import java.lang.System
import scala.language.postfixOps

// todo: maybe move some utils method outside
class MornyInformation (using coeur: MornyCoeur) extends ITelegramCommand {
	
	private case object Subs {
		val STICKERS = "stickers"
		val RUNTIME = "runtime"
		val VERSION = "version"
		val VERSION_2 = "v"
		val TASKS = "tasks"
	}
	
	override val name: String = "info"
	override val aliases: Array[ICommandAlias]|Null = null
	override val paramRule: String = "[(version|runtime|stickers[.IDs]|tasks)]"
	override val description: String = "输出当前 Morny 的各种信息"
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		if (command.args isEmpty) {
			echoInfo(event.message.chat.id, event.message.messageId)
			return
		}
		
		val action: String = command.args(0)
		
		action match {
			case s if s startsWith Subs.STICKERS => echoStickers
			case Subs.RUNTIME => echoRuntime
			case Subs.VERSION | Subs.VERSION_2 => echoVersion
			case Subs.TASKS => echoTasksStatus
			case _ => echo404
		}
		
	}
	
	private def echoInfo (chatId: Long, replyTo: Int): Unit = {
		coeur.account exec new SendPhoto(
			chatId,
			getAboutPic
		).caption(
			s"""<b>Morny Cono</b>
			   |来自安妮的侍从小鼠。
			   |————————————————
			   |$getMornyAboutLinksHTML"""
					.stripMargin
		).parseMode(ParseMode HTML).replyToMessageId(replyTo)
	}
	
	private def echoStickers (using command: InputCommand, event: Update): Unit = {
		val mid: String|Null =
			if (command.args(0) == Subs.STICKERS) {
				if (command.args.length == 1) ""
				else if (command.args.length == 2) command.args(1)
				else null
			} else if (command.args.length == 1) {
				if ((command.args(0) startsWith s"${Subs.STICKERS}.") || (command.args(0) startsWith s"${Subs.STICKERS}#")) {
					command.args(0) substring Subs.STICKERS.length+1
				} else null
			} else null
		if (mid == null) echo404
		else echoStickers(mid)(using event.message.chat.id, event.message.messageId)
	}
	
	private def echoStickers (mid: String)(using send_chat: Long, send_replyTo: Int)(using Update): Unit = {
		import scala.jdk.CollectionConverters.*
		if (mid isEmpty) for ((_key, _file_id) <- TelegramStickers.map asScala)
			echoSticker(_key, _file_id)
		else {
			try {
				val sticker = TelegramStickers getById mid
				echoSticker(sticker.getKey, sticker.getValue)
			} catch case _: NoSuchFieldException => {
				echo404
			}
		}
	}
	
	private def echoSticker (mid: String, file_id: String)(using send_chat: Long, send_replyTo: Int): Unit = {
		val send_mid = SendMessage(send_chat, mid)
		val send_sticker = SendSticker(send_chat, file_id)
		if (send_replyTo != -1) send_mid.replyToMessageId(send_replyTo)
		val result_send_mid = coeur.account exec send_mid
		send_sticker.replyToMessageId(result_send_mid.message.messageId)
		coeur.account exec send_sticker
	}
	
	private[command] def echoVersion (using event: Update): Unit = {
		val versionDeltaHTML = if (MornySystem.isUseDelta) s"-δ<code>${h(MornySystem.VERSION_DELTA)}</code>" else ""
		val versionGitHTML = if (MornySystem.isGitBuild) s"git $getVersionGitTagHTML" else ""
		coeur.account exec new SendMessage(
			event.message.chat.id,
			// language=html
			s"""version:
			   |- Morny <code>${h(MornySystem.CODENAME toUpperCase)}</code>
			   |- <code>${h(MornySystem.VERSION_BASE)}</code>$versionDeltaHTML${if (MornySystem.isGitBuild) "\n- " + versionGitHTML else ""}
			   |coeur md5_hash:
			   |- <code>${h(MornySystem.getJarMD5)}</code>
			   |coding timestamp:
			   |- <code>${BuildConfig.CODE_TIMESTAMP}</code>
			   |- <code>${h(formatDate(BuildConfig.CODE_TIMESTAMP, 0))} [UTC]</code>
			   |""".stripMargin
		).replyToMessageId(event.message.messageId).parseMode(ParseMode HTML)
	}
	
	private[command] def echoRuntime (using event: Update): Unit = {
		def sysprop (p: String): String = System.getProperty(p)
		coeur.account exec new SendMessage(
			event.message.chat.id,
			/* language=html */
			s"""system:
			   |- <code>${h(if getRuntimeHostname nonEmpty then getRuntimeHostname.get else "<unknown-host>")}</code>
			   |- <code>${h(sysprop("os.name"))}</code> <code>${h(sysprop("os.arch"))}</code> <code>${h(sysprop("os.version"))}</code>
			   |java runtime:
			   |- <code>${h(sysprop("java.vm.vendor"))}.${h(sysprop("java.vm.name"))}</code>
			   |- <code>${h(sysprop("java.vm.version"))}</code>
			   |vm memory:
			   |- <code>${Runtime.getRuntime.totalMemory/1024/1024}</code> / <code>${Runtime.getRuntime.maxMemory/1024/1024}</code> MB
			   |- <code>${Runtime.getRuntime.availableProcessors}</code> cores
			   |coeur version:
			   |- $getVersionAllFullTagHTML
			   |- <code>${h(MornySystem.getJarMD5)}</code>
			   |- <code>${h(formatDate(BuildConfig.CODE_TIMESTAMP, 0))} [UTC]</code>
			   |- [<code>${BuildConfig.CODE_TIMESTAMP}</code>]
			   |continuous:
			   |- <code>${h(formatDuration(System.currentTimeMillis - coeur.coeurStartTimestamp))}</code>
			   |- [<code>${System.currentTimeMillis - coeur.coeurStartTimestamp}</code>]
			   |- <code>${h(formatDate(coeur.coeurStartTimestamp, 0))}</code>
			   |- [<code>${coeur.coeurStartTimestamp}</code>]"""
			.stripMargin
		).parseMode(ParseMode HTML).replyToMessageId(event.message.messageId)
	}
	
	private def echoTasksStatus (using update: Update): Unit = {
//		if !coeur.trusted.isTrusted(update.message.from.id) then return;
		coeur.account exec SendMessage(
			update.message.chat.id,
			// language=html
			s"""<b>Coeur Task Scheduler:</b>
			   | - <i>scheduled tasks</i>: <code>${coeur.tasks.amount}</code>
			   | - <i>scheduler status</i>: <code>${coeur.tasks.state}</code>
			   | - <i>current runner status</i>: <code>${coeur.tasks.runnerState}</code>
			   |""".stripMargin
		).parseMode(ParseMode.HTML).replyToMessageId(update.message.messageId)
	}
	
	private def echo404 (using event: Update): Unit =
		coeur.account exec new SendSticker(
			event.message.chat.id,
			TelegramStickers ID_404
		).replyToMessageId(event.message.messageId)
	
}
