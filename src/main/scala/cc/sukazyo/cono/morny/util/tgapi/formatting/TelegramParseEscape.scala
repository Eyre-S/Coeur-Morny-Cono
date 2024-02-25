package cc.sukazyo.cono.morny.util.tgapi.formatting

import org.jsoup.Jsoup
import org.jsoup.nodes.Node

import scala.collection.mutable
import scala.jdk.CollectionConverters.*

object TelegramParseEscape {
	
	/** Encoded a [[String]] that make it can be used as plain texts in Telegram HTML.
	  *
	  * This method will just remove `&`, `<` and `>` characters and encode them to
	  * HTML entities.
	  *
	  * @since 1.0.0
	  */
	def escapeHtml (input: String): String =
		var process = input
		process = process.replaceAll("&", "&amp;")
		process = process.replaceAll("<", "&lt;")
		process = process.replaceAll(">", "&gt;")
		process
	
	/** Transform a [[String]] encoded HTML document/fragment into a Telegram capable
	  * HTML fragment, make it can be used in sending Telegram message etc.
	  *
	  * This method will remove all unsupported HTML tags and attributes. Here is the
	  * specific rule list of how to process the tags:
	  *
	  *  - If the tag is supported by Telegram HTML, it will be kept, and the inner HTML
	  *    children items will be processed recursively.
	  *  - If the tag is `<br>`, it will be converted to a newline character (`\n`). If
	  *    there's any children in the `<br>` tag (which is not allowed in HTML), they
	  *    will just be ignored.
	  *  - If the tag is `<img>`, it will be removed. If the `alt` attribute is present,
	  *    a string `"[$alt]"` will be used as the replacement text.
	  *  - If the tag is any other tag, it will be removed, and the inner HTML children
	  *    will be kept and processed recursively.
	  *
	  * @since 1.3.0
	  */
	def cleanupHtml (input: String): String =
		import org.jsoup.nodes.*
		val source = Jsoup.parse(input)
		val x = cleanupHtml(source.body.childNodes.asScala.toSeq)
		val doc = Document("")
		doc.outputSettings
			.prettyPrint(false)
		x.map(f => doc.appendChild(f))
		x.mkString("")
	
//	def toHtmlRaw (input: Node): String =
//		import org.jsoup.nodes.*
//		input match
//			case text: TextNode => text.getWholeText
//			case _: (DataNode | XmlDeclaration | DocumentType | Comment) => ""
//			case elem: Element => elem.childNodes.asScala.map(f => toHtmlRaw(f)).mkString("")
	
	private def cleanupHtml (input: Seq[Node]): List[Node] =
		val result = mutable.ListBuffer.empty[Node]
		for (i <- input) {
			import org.jsoup.nodes.*
			def produceChildNodes (curr: Element): Element =
				val newOne = Element(curr.tagName)
				curr.attributes.forEach(attr => newOne.attr(attr.getKey, attr.getValue))
				for (i <- cleanupHtml(curr.childNodes.asScala.toSeq)) newOne.appendChild(i)
				newOne
			i match
				case text_cdata: CDataNode => result += CDataNode(text_cdata.text)
				case text: TextNode => result += TextNode(text.getWholeText)
				case _: (DataNode | XmlDeclaration | DocumentType | Comment) =>
				case elem: Element => elem match
					case _: Document => // should not exists here
					case _: FormElement => // ignored due to Telegram do not support form
					case elem => elem.tagName match
						case "a"|"b"|"strong"|"i"|"em"|"u"|"ins"|"s"|"strike"|"del"|"tg-spoiler"|"code"|"pre" =>
							result += produceChildNodes(elem)
						case "br" =>
							result += TextNode("\n")
						case "tg-emoji" =>
							if elem.attributes `hasKey` "emoji-id" then
								result += produceChildNodes(elem)
							else
								result += TextNode(elem.text)
						case "img" =>
							if elem.attributes `hasKey` "alt" then
								result += TextNode(s"[${elem.attr("alt")}]")
						case _ =>
							for (i <- cleanupHtml(elem.childNodes.asScala.toSeq)) result += i
		}
		result.toList
	
}
