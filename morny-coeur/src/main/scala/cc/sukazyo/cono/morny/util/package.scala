package cc.sukazyo.cono.morny

/** Utils that [[cc.sukazyo.cono.morny]]'s code used.
  *
  * contains:
  *   - [[tgapi Telegram API/Utils Extras]]
  *   - extensions of language standard
  *     - [[CommonEncrypt]] re-encapsulated some encrypt algorithms, and some normalized while encrypting.
  *     - [[CommonFormat]] provides some format methods normalized based on Morny usage standard.
  *     - [[ConvertByteHex]] extensions [[Byte]] and so on, make it easier converting binary data in it to a hex string.
  *     - [[UseMath]] scala style to make Math function easier to use
  *     - [[UseRandom]] scala style to use Random to generate something
  *   - external library extras
  *     - [[OkHttpPublic]] defines some static value for [[okhttp3]]
  *   - useful misc utils
  *     - [[FileUtils]] contains some easy-to-use file action.
  *     - [[UniversalCommand]] provides a easy way to get an args array from a string input.
  *   - others
  *     - [[BiliTool about Bilibili]]
  *
  */
package object util {}
