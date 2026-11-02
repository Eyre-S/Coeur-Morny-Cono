package cc.sukazyo.cono.morny.system.telegram_api.message

/** Message that is not recognized by the system.
  *
  * A native message that is not supported yet will be transformed to this.
  *
  * @since 2.0.0-alpha22
  */
trait UnknownMessage extends Message
