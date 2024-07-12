package cc.sukazyo.cono.morny.system.telegram_api.formatting

import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramParseEscape.escapeHtml as h
import cc.sukazyo.cono.morny.system.telegram_api.Standardize.MASK_BOTAPI_FORMATTED_ID
import com.pengrad.telegrambot.model.{Chat, Message, User}
import com.pengrad.telegrambot.model.Chat.Type

object TelegramFormatter {
	
	extension (chat: Chat) {
		
		def safe_name: String = chat.`type` match
			case Type.Private => _connectName(chat.firstName, chat.lastName)
			case _ => chat.title
		
		def safe_linkHTML: String =
			if (chat.username == null)
				chat.`type` match
					// language=html
					case Type.Private => s"<a href='${_link_user(chat.id)}'>@[u:${chat.id}]</a>"
					// language=html
					case _ => s"<a href='${_link_chat(chat.id_tdLib)}'>@[c/${chat.id}]</a>"
			else s"@${h(chat.username)}"
		
		//noinspection ScalaWeakerAccess
		def safe_firstnameRefHTML: String =
			chat.`type` match
				// language=html
				case Type.Private => s"<a href='${_link_user(chat.id)}'>${h(chat.firstName)}</a>"
				// language=html
				case _ => s"<a href='${_link_chat(chat.id_tdLib)}'>${h(chat.title)}</a>"
		
		//noinspection ScalaWeakerAccess
		def id_tdLib: Long =
			if chat.id < 0 then (chat.id - MASK_BOTAPI_FORMATTED_ID)abs else chat.id
		
		def typeTag: String =
			import ChatTypeTag.tag
			chat.`type`.tag
		
	}
	
	object ChatTypeTag {
		
		inline val PRIVATE = "🔒"
		inline val GROUP = "💭"
		inline val SUPERGROUP = "💬"
		inline val CHANNEL = "📢"
		
		extension (t: Type) {
			def tag: String = t match
				case Type.Private => this.PRIVATE
				case Type.group => this.GROUP
				case Type.supergroup => this.SUPERGROUP
				case Type.channel => this.CHANNEL
		}
		
	}
	
	extension (user: User) {
		
		//noinspection ScalaWeakerAccess
		def fullname: String = _connectName(user.firstName, user.lastName)
		
		def fullnameRefHTML: String =
			// language=html
			s"<a href='${_link_user(user.id)}'>${h(user.fullname)}</a>"
		
		//noinspection ScalaWeakerAccess
		def firstnameRefHTML: String =
			// language=html
			s"<a href='${_link_user(user.id)}'>${h(user.firstName)}</a>"
		
		def toLogTag: String =
			(if (user.username == null) user.fullname + " " else "@" + user.username)
					+ "[" + user.id + "]"
		
	}
	
	extension (m: Message) {
		
		def sender_id: Long =
			if m.senderChat == null then m.from.id else m.senderChat.id
		
		def sender_firstnameRefHTML: String =
			if (m.senderChat == null)
				m.from.firstnameRefHTML
			else m.senderChat.safe_firstnameRefHTML
		
	}
	
	private inline def _link_user (id: Long): String =
		s"tg://user?id=$id"
	
	private inline def _link_chat (id: Long): String =
		s"https://t.me/c/$id"
	
	private inline def _connectName (firstName: String, lastName: String): String =
		firstName + (if lastName == null then "" else " " + lastName)
	
}
