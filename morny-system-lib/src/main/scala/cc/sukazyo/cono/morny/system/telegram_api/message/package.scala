package cc.sukazyo.cono.morny.system.telegram_api

/** The well-formed message types that may be received from Telegram Bot API.
  *
  * Currently under version Bot API 9.1
  *
  * Provides different well-formed message types, such as text message, photo message, video
  * message, etc.
  *
  * Provides helpers to creating a message, or derive a new message (with reply parameters
  * or without it) with an existing message.
  *
  * Along with [[cc.sukazyo.cono.morny.system.telegram_api.action.TelegramActions]], provides
  * a complete set of tools to handle messages received from Telegram Bot API, and send
  * messages to Telegram Bot API.
  *
  * @see [[Messages]] helpers to create messages.
  *
  * @since 2.0.0-alpha22
  *
  * @todo some types may not be implemented or missing data field
  */
package object message
