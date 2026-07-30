package cc.sukazyo.cono.morny.system.telegram_api

import com.pengrad.telegrambot.model.Message

/** @define globalUnique
  *
  * This type of numeric ID is unique, unchangeable, and unreusable for a specific item. It
  * should also stay the same across bots/accounts. This means that if you have a UserID
  * for a user in one bot, you will get the same UserID for that user in any other bot
  * (as long as they are both using the same Telegram account or are authorized to access
  * the same data).
  *
  * > The community believes that those IDs are self-incrementing numerics, means that it will
  * > never being reused, and the later one will always have larger ID than earlier one. In
  * > practice, someone did make a program that uses known accounts ids with their registration
  * > datetime to calculate any accounts registration datetime.
  * >
  * > For now, it is same across accounts, but Telegram's developer seems does not guarantee
  * > that ([source](https://github.com/tdlib/td/issues/2591#issuecomment-1703096885)).
  * @define int52
  *
  * According to documentations, these type of numeric IDs is a 53-bit integer (signed int53),
  * obviously, Java does not have an int52 type. Luckily, Java has a 64-bit integer called
  * [[Long]]. So that, this is stored in [[Long]] in Java.
  * @define int32or52
  *
  * In TDLib, it is stored with signed int53, but according to Bots API, integer fields
  * "without otherwise noted" is safe to use signed int32 to store.
  * [source](https://core.telegram.org/bots/api#available-types) So it should be safe to store
  * with int32 or [[Int]].
  */
object Standardize {
	
	/** Internal numeric ID indicates to a user.
	  *
	  * $globalUnique
	  *
	  * Special numbers:
	  *
	  * - [[CHANNEL_SPEAKER_MAGIC_ID]]
	  *
	  * $int52
	  */
	type UserID = Long
	
	/** Internal numeric ID indicates to a chat.
	  *
	  * Chat id may be also a [[UserID]], in this case, it indicates a private chat with that
	  * user.
	  *
	  * $globalUnique
	  *
	  * For supergroups and channels, their chat id may be different under different API
	  * specification (or maybe user type), for more details, see [[MASK_BOTAPI_FORMATTED_ID]].
	  *
	  * $int52
	  */
	type ChatID = Long
	
	/** Internal numeric ID indicates to a message thread.
	  *
	  * A message thread is a sub-chat of one chat (for supergroup only) (Below, I may talk
	  * about this with "message thread" and "sub-chat" both, it is the same thing). Like
	  * [[MessageID]], they are meaningful only under a specific chat, so that the message
	  * thread id only keep unique under their own chat.
	  *
	  * For a chat that does not have sub-chats, there are no message thread ID defined.
	  *
	  * A chat that has sub-chats will always have a sub-chat "#General", its message thread ID
	  * will always be [[1]]. For other sub-chats, it will consume one [[MessageID]] to create,
	  * and that message id will become their message thread id.
	  *
	  * > For example, if one chat's latest message's ID is [[100]], creating a new sub-chat
	  * > will consume chat id [[101]] -- ID [[101]] will be the new sub-chat's ID, and the
	  * > next message's ID will be [[102]].
	  * >
	  * > Since creating a message thread requires send a message to that new thread, so that
	  * > it actually consumes two message id, smaller one (101) will be the message thread id,
	  * > the larger one (102) will be the first message in that thread.
	  *
	  * So that, in principle, a message thread id and a message id (in their chat) will never
	  * be the same. And since [[MessageID]] is self-incrementing, message thread id should be
	  * also unique in time (that does not recycle).
	  *
	  * $int32or52
	  */
	type MessageThreadID = Long
	
	/** Internal numeric ID indicates to a message thread.
	  *
	  * Message ids are unique and self-incrementing **in their scope**，in that scope, message
	  * ids will never reuse even that message have deleted.
	  *
	  * A supergroup (no matter message threads) or a channel is a scope. All private chats
	  * share one scope under current logged-in user.
	  *
	  * > You can find more details about the scope.
	  *
	  * ## Multi media message
	  *
	  * The underlying message format support only one media (photo, video, etc.) in one
	  * message, so that a message group (a message that has multiple pictures, videos, files
	  * etc.) actually have multiple message ids combined with a [[MessageGroupID]].
	  *
	  * For more details, see [[MessageGroupID]].
	  *
	  * ## Message scopes
	  *
	  * A message scope is virtual (actually named by me)concept, means that only in there,
	  * message ids are unique and self-incremental. Just like array indexes are only available
	  * in their specific array.
	  *
	  * A message scope is where Telegram server actually stores message. Every message in
	  * server can be located with a unique (messageScope, messageId) key.
	  *
	  * A supergroup or a channel have its own message scope.
	  *
	  * ### Message id in private chats
	  *
	  * Now, it's time to talk about messages in private chats. Unlike supergroups or channels,
	  * private chats its own have no message scope. They cannot store messages. Alternatively,
	  * messages there are stored in user space, we can call that space **user scope**. Every
	  * user have its own user message scope, and stores all the messages they received from
	  * other users.
	  *
	  * For example, when user A send message to user B, the message is stored in user A's
	  * user message scope; and then that message is copied and the user B will receive a copy
	  * and store the copy to user B's user message scope. At last, user A and user B both have
	  * one copy of the message, they look like the same, but fundamentally are different
	  * objects in server. A big different with supergroup messages (whereas messages stores
	  * directly to the group's scope, and all joined user have permission to access it),
	  * private messages are more like e-mails.
	  *
	  * > That's why you can delete a message in private chat only for yourself, without
	  * > affecting the other side user.
	  * >
	  * > The non-super groups are also works like this (like mail-list?), but since bots can
	  * > not join non-super groups, we can ignore talking about it.
	  *
	  * Since both have its own copy, and stores in its own message scope, so, for one message,
	  * the message id of sender and receiver are different; and for one user, messages between
	  * private chats share the same message scope -- message ids are incremental and unique
	  * across private chats.
	  *
	  * ### Supergroups that have message threads
	  *
	  * Message threads are more like a tag of message, they do not affect how message id works.
	  * A supergroup with all of its message threads share one message scope, message ids are
	  * incremental and unique across message chats.
	  *
	  * ## Special numbers
	  *
	  * - [[NON_COMMITED_MESSAGE_ID]] : If a message is sent but not fully commited.
	  *
	  * ## Data type
	  *
	  * $int32or52
	  */
	type MessageID = Int
	/** ID to combine multiple messages into one big visual message.
	  *
	  * Telegram does not support multiple media in one message. This thing is a workaround to
	  * make multiple messages looks like one big message, that have multiple medias in it.
	  *
	  * Messages that in one same group will have the same message group id, they *should*
	  * always be able to read in one update. It *should* not happen that messages from two
	  * different updates have the same id.
	  */
	type MessageGroupID = String
	
	/** ID that indicates a file in Telegram Server. It can be reused to send the same file
	  * again without reuploading them.
	  *
	  * This type of ID is limited to the same bot, it cannot be reused by another bot.
	  *
	  * > But this limitation seems to have exceptions, it seems that a bot can download a file
	  * > with an ID that comes from another bot.
	  *
	  * File IDs are not unique, for one specific file, every API calls will almost certainly
	  * return different file ids. To check if two file IDs refer to the same file, use
	  * [[FileUniqueID]] instead.
	  *
	  * @see For limitations about reusing file IDs, see `IDBased` section of
	  *      [[objects.ClientMediaData!]].
	  */
	type FileID = String
	
	/** ID that uniquely identifies a file.
	  *
	  * It stays the same for the same file, even across different bots. So that it can be used
	  * to check if two file is the same file.
	  *
	  * It cannot be used to get or resend a file, that can only be done with [[FileID]].
	  */
	type FileUniqueID = String
	
	/** Time format that Telegram Bot API use.
	  *
	  * UTC+0 seconds since 1970-01-01 00:00:00, also known as Unix time. Normally stored as
	  * int.
	  */
	type ServerTime = Int
	
	/** A fake user id when a "channel as a user" is speaking in chat.
	  *
	  * In some version, Telegram introduce a feature that, user who has channels can talk in
	  * chat using the channel identity, so that their real account will not be shown up. When
	  * this kind of "user" is talking, the [[Message]] entity will have a [[Message.senderChat]]
	  * field to show the speaker channel identity, wherease the [[Message.from]] field will be
	  * a fake user with this id for backward capability.
	  */
	val CHANNEL_SPEAKER_MAGIC_ID: UserID = 136817688
	
	/** Supergroup/channel chat id mask for Bot API formatted chat id.
	  *
	  * (if we assume that TDLib's chat id is the original/real chat id)
	  *
	  * For supergroups and channels, the chat id in Bot API is the original chat id with a
	  * decimal mask [[MASK_BOTAPI_FORMATTED_ID]].
	  *
	  * > What the *original id* means the `dialog_id` that TDLib returns. `dialog_id` is one
	  * > that functions the same with Bot API chat id, which is also negative number (maybe
	  * > for differences to user's dialog_id).
	  * >
	  * > The formular to convert is basically: `chat_id = MASK + dialog_id`.
	  * >
	  * > Note that I haven't tested, but seems someone says that when a bot login when TDLib
	  * > API, it still uses BotAPI formatted chat id ([source][mt-bot-chat-id]).
	  * >
	  * > Even real user should have a dialog_id without that mask (-100 prefix), but most of
	  * > the clients have patched that to make their chat id shows the same as BotAPI
	  * > formatted chat id (maybe for user convenience of developing bots). If you see a chat
	  * > id that has a `-100` prefix, it is likely (almost certainly) BotAPI chat id, is safe
	  * > to use here without convertion that mentioned above.
	  *
	  * [mt-bot-chat-id]: https://github.com/tdlib/td/issues/2591#issuecomment-1701848908
	  */
	val MASK_BOTAPI_FORMATTED_ID: ChatID = -1000000000000L
	
	/** A message id that indicates the message is not fully sent (commited to Telegram DC).
	  *
	  * Normally when you [[com.pengrad.telegrambot.request.AbstractSendRequest send any messages]],
	  * API will return you the message that you sent, with additional server only status.
	  *
	  * But if the Telegram DataCenter got stuck or any other cases, message sent to API may
	  * not completely commited to Telegram in time. This is really rare, but if it happens,
	  * API will return the (not fully) sent message with this id, so that you can know that
	  * message is not fully sent and may miss some server-only status.
	  *
	  * Message that returns with this id can be shows as a "Pending" status.
	  */
	val NON_COMMITED_MESSAGE_ID: MessageID = 0
	
}
