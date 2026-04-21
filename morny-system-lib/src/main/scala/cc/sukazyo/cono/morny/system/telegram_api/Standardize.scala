package cc.sukazyo.cono.morny.system.telegram_api

/** @define NumericIDs
  *
  * The type of numeric ID is unique, unchangeable, and unreusable.
  *
  * According to documentations, these type of numeric IDs is a 52-bit integer (int52),
  * obviously, Java does not have an int52 type. Luckily, Java has a 64-bit integer called
  * [[Long]]. So that, this is stored in [[Long]] in Java.
  */
object Standardize {
	
	/** Internal numeric ID indicates to a user.
	  * $NumericIDs
	  */
	type UserID = Long
	/** Internal numeric ID indicates to a chat.
	  *
	  * Chat id may be also a [[UserID]], in this case, it indicates a private chat with that
	  * user.
	  *
	  * For supergroups and channels, the chat id Bot API can use is the original chat id with
	  * a decimal mask [[MASK_BOTAPI_FORMATTED_ID]].
	  *
	  * $NumericIDs
	  */
	type ChatID = Long
	type MessageThreadID = Long
	type MessageID = Int
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
	
	type ServerTime = Int
	
	val CHANNEL_SPEAKER_MAGIC_ID: UserID = 136817688
	
	val MASK_BOTAPI_FORMATTED_ID: ChatID = -1000000000000L
	
	val NON_COMMITED_MESSAGE_ID: MessageID = 0
	
}
