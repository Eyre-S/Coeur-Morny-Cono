package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.data.{TelegramImages, TelegramStickers}
import cc.sukazyo.cono.morny.util.CommonFormat.{formatDate, formatDuration}
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape.escapeHtml as h
import cc.sukazyo.cono.morny.{BuildConfig, MornyAbout, MornyCoeur, MornySystem}
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{SendMessage, SendPhoto, SendSticker}

import java.lang.System
import java.net.InetAddress
import java.rmi.UnknownHostException
import scala.language.postfixOps

object MornyInformation extends ITelegramCommand {
	
	private case object Subs {
		val STICKERS = "stickers"
		val RUNTIME = "runtime"
		val VERSION = "version"
		val VERSION_2 = "v"
	}
	
	override val name: String = "info"
	override val aliases: Array[ICommandAlias]|Null = null
	override val paramRule: String = "[(version|runtime|stickers[.IDs])]"
	override val description: String = "输出当前 Morny 的各种信息"
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		if (!command.hasArgs) {
			echoInfo(event.message.chat.id, event.message.messageId)
			return
		}
		
		val action: String = command.getArgs()(0)
		
		action match {
			case Subs.STICKERS => echoStickers
			case Subs.RUNTIME => echoRuntime
			case Subs.VERSION | Subs.VERSION_2 => echoVersion
			case _ => echo404
		}
		
	}
	
	def getVersionGitTagHTML: String = {
		if (!MornySystem.isGitBuild) return ""
		val g = StringBuilder()
		val cm = BuildConfig.COMMIT substring(0, 8)
		val cp = MornySystem.currentCodePath
		if (cp == null) g++= s"<code>$cm</code>"
		else g++= s"<a href='$cp'>$cm</a>"
		if (!MornySystem.isCleanBuild) g++= ".<code>δ</code>"
		g toString
	}
	
	def getVersionAllFullTagHTML: String = {
		val v = StringBuilder()
		v ++= s"<code>${MornySystem VERSION_BASE}</code>"
		if (MornySystem isUseDelta) v++=s"-δ<code>${MornySystem VERSION_DELTA}</code>"
		if (MornySystem isGitBuild) v++="+"++=getVersionGitTagHTML
		v ++= s"*<code>${MornySystem.CODENAME toUpperCase}</code>"
		v toString
	}
	
	def getRuntimeHostname: String|Null = {
		try InetAddress.getLocalHost.getHostName
		catch case _:UnknownHostException => null
	}
	
	def getAboutPic: Array[Byte] = TelegramImages.IMG_ABOUT get
	
	def getMornyAboutLinksHTML: String =
		s"""<a href='${MornyAbout MORNY_SOURCECODE_LINK}'>source code</a> | <a href='${MornyAbout MORNY_SOURCECODE_SELF_HOSTED_MIRROR_LINK}'>backup</a>
		   |<a href='${MornyAbout MORNY_ISSUE_TRACKER_LINK}'>反馈 / issue tracker</a>
		   |<a href='${MornyAbout MORNY_USER_GUIDE_LINK}'>使用说明书 / user guide & docs</a>"""
		.stripMargin
	
	private def echoInfo (chatId: Long, replyTo: Int): Unit = {
		MornyCoeur.extra exec new SendPhoto(
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
		val chat = event.message.chat.id
		val replyTo = event.message.messageId
		var sid: String|Null = null
		if (command.getArgs()(0) == Subs.STICKERS) {
			if (command.getArgs.length == 1) sid = ""
			else if (command.getArgs.length == 2) sid = command.getArgs()(1)
		} else if (command.getArgs.length == 1) {
			if ((command.getArgs()(0) startsWith s"${Subs.STICKERS}.") || (command.getArgs()(0) startsWith s"${Subs.STICKERS}#")) {
				sid = command.getArgs()(0) substring Subs.STICKERS.length+1
			}
		}
		if (sid == null) echo404
		else echoStickers(sid, chat, replyTo)
	}
	
	private def echoStickers (sid: String, send_chat: Long, send_replyTo: Int): Unit = {
		if (sid isEmpty) TelegramStickers echoAllStickers(MornyCoeur.extra, send_chat, send_replyTo)
		else TelegramStickers echoStickerByID(sid, MornyCoeur.extra, send_chat, send_replyTo)
	}
	
	private[command] def echoVersion (using event: Update): Unit = {
		val versionDeltaHTML = if (MornySystem.isUseDelta) s"-δ<code>${h(MornySystem.VERSION_DELTA)}</code>" else ""
		val versionGitHTML = if (MornySystem.isGitBuild) s"git $getVersionGitTagHTML" else ""
		MornyCoeur.extra exec new SendMessage(
			event.message.chat.id,
			// language=html
			s"""version:
			   |- Morny <code>${h(MornySystem.CODENAME toUpperCase)}</code>
			   |- <code>${h(MornySystem.VERSION_BASE)}</code>$versionDeltaHTML${if (MornySystem.isGitBuild) "\n- " + versionGitHTML else ""}
			   |coeur md5_hash:
			   |- <code>${h(MornySystem.getJarMd5)}</code>
			   |coding timestamp:
			   |- <code>${BuildConfig.CODE_TIMESTAMP}</code>
			   |- <code>${h(formatDate(BuildConfig.CODE_TIMESTAMP, 0))} [UTC]</code>
			   |""".stripMargin
		).replyToMessageId(event.message.messageId).parseMode(ParseMode HTML)
	}
	
	private[command] def echoRuntime (using event: Update): Unit = {
		def sysprop (p: String): String = System.getProperty(p)
		MornyCoeur.extra exec new SendMessage(
			event.message.chat.id,
			/* language=html */
			s"""system:
			   |- Morny <code>${h(if (getRuntimeHostname == null) "<unknown-host>" else getRuntimeHostname)}</code>
			   |- <code>${h(sysprop("os.name"))}</code> <code>${h(sysprop("os.arch"))}</code> <code>${h(sysprop("os.version"))}</code>
			   |java runtime:
			   |- <code>${h(sysprop("java.vm.vendor"))}.${h(sysprop("java.vm.name"))}</code>
			   |- <code>${h(sysprop("java.vm.version"))}</code>
			   |vm memory:
			   |- <code>${Runtime.getRuntime.totalMemory/1024/1024}</code> / <code>${Runtime.getRuntime.maxMemory/1024/1024}</code>
			   |- <code>${Runtime.getRuntime.availableProcessors}</code> cores
			   |coeur version:
			   |- $getVersionAllFullTagHTML
			   |- <code>${h(MornySystem.getJarMd5)}</code>
			   |- <code>${h(formatDate(BuildConfig.CODE_TIMESTAMP, 0))} [UTC]</code>
			   |- [<code>${BuildConfig.CODE_TIMESTAMP}</code>]
			   |continuous:
			   |- <code>${h(formatDuration(System.currentTimeMillis - MornyCoeur.coeurStartTimestamp))}</code>
			   |- [<code>${System.currentTimeMillis - MornyCoeur.coeurStartTimestamp}</code>]
			   |- <code>${h(formatDate(MornyCoeur.coeurStartTimestamp, 0))}</code>
			   |- [<code>${MornyCoeur.coeurStartTimestamp}</code>]"""
			.stripMargin
		).parseMode(ParseMode HTML).replyToMessageId(event.message.messageId)
	}
	
	private def echo404 (using event: Update): Unit =
		MornyCoeur.extra exec new SendSticker(
			event.message.chat.id,
			TelegramStickers ID_404
		).replyToMessageId(event.message.messageId)
	
}
