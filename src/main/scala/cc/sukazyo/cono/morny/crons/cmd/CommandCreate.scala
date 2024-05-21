package cc.sukazyo.cono.morny.crons.cmd

import cc.sukazyo.cono.morny.core.bot.api.{ICommandAlias, ISimpleCommand}
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.messages.{ErrorMessage, MessagingContext}
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import cc.sukazyo.cono.morny.util.UseThrowable.toLogString
import com.cronutils.descriptor.CronDescriptor
import com.cronutils.model.{Cron, CronType}
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import com.pengrad.telegrambot.model.{Message, Update}
import com.pengrad.telegrambot.request.{SendMessage, SendSticker}

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CommandCreate (using coeur: MornyCoeur) extends ISimpleCommand {
	import coeur.dsl.{*, given}
	
	override val name: String = "create_cron_message"
	override val aliases: List[ICommandAlias] = Nil
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		given context: MessagingContext.WithUserAndMessage = MessagingContext.extract(using event.message)
		
		SendMessage(
			context.bind_chat.id,
			"Type your CRON expression below:"
		).replyToMessageId(context.bind_message.messageId)
			.unsafeExecute
		
		coeur.messageThreading.doAfter(execute1)
		
	}
	
	def execute1 (message: Message, prev: MessagingContext.WithUserAndMessage): Unit = {
		given context: MessagingContext.WithUserAndMessage = MessagingContext.extract(using message)
		
		val cron: Cron =
			try CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX))
				.parse(message.text())
			catch case e: IllegalArgumentException =>
				ErrorMessage(
					SendSticker(
						context.bind_chat.id,
						TelegramStickers.ID_404
					),
					SendMessage(
						context.bind_chat.id,
						s"""Probably you've entered an invalid CRON expression:
						   |
						   |${e.toLogString}
						   |""".stripMargin
					)
				).submit
				return
		
		val cron_describe = CronDescriptor.instance().describe(cron)
		val current = ZonedDateTime.now()
		val cron_exec_time = ExecutionTime.forCron(cron)
		val cron_next = cron_exec_time.nextExecution(current)
		val cron_prev = cron_exec_time.lastExecution(current)
		SendMessage(
			context.bind_chat.id,
			s"""Set CRON task.
			   |
			   |Your cron settings is runs $cron_describe
			   |Previous runs at: ${cron_prev.map(_.format(DateTimeFormatter.ISO_DATE_TIME)).orElse("Will not run before.")}
			   |Next will run at: ${cron_next.map(_.format(DateTimeFormatter.ISO_DATE_TIME)).orElse("Will not run afterwards.")}
			   |
			   |""".stripMargin
		).replyToMessageId(context.bind_message.messageId)
			.unsafeExecute
		
	}
	
}
