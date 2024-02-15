package cc.sukazyo.cono.morny.util.tgapi.formatting

import org.jsoup.Jsoup
import org.jsoup.nodes.Node

import scala.collection.mutable
import scala.jdk.CollectionConverters.*

object TelegramParseEscape {
	
	def escapeHtml (input: String): String =
		var process = input
		process = process.replaceAll("&", "&amp;")
		process = process.replaceAll("<", "&lt;")
		process = process.replaceAll(">", "&gt;")
		process
	
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
	
	def cleanupHtml (input: Seq[Node]): List[Node] =
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
