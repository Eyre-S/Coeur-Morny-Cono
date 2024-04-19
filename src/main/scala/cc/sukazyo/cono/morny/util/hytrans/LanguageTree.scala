package cc.sukazyo.cono.morny.util.hytrans

import cc.sukazyo.cono.morny.util.hytrans.LangTag.IllegalLangTagException

import java.io.{PrintWriter, StringWriter}
import scala.collection.mutable
import scala.util.boundary

class LanguageTree {
	
	class Node (val langTag: LangTag) extends Comparable[Node] {
		private object LangTagOrdering extends Ordering[Node]:
			override def compare (x: Node, y: Node): Int = y.langTag `compareTo` x.langTag
		
		override def compareTo (o: Node): Int = this.langTag `compareTo` o.langTag
		
		private type ChildCol = mutable.SortedSet[Node]
		
		private var _parent: Option[Node] = None
		private val _children: ChildCol = mutable.SortedSet.empty(using LangTagOrdering)
		
		def parent: Option[Node] = _parent
		def children: ChildCol = _children
		
		def traverseParent (f: Node => Unit): Unit =
			_parent.foreach { parent =>
				f(parent)
				parent.traverseParent(f)
			}
		
		private def traversingTree (f: Node => Unit)(using visited: mutable.Set[Node]): Unit =
			_children.foreach { child =>
				if !(visited contains child) then
					child.traversingTree(f)
			}
			if !(visited contains this) then
				f(this)
				visited += this
			this.parent.foreach { parent =>
				if !(visited contains parent) then
					parent.traversingTree(f)
			}
		
		def traverseTree (f: Node=>Unit): Unit =
			val visited = mutable.Set.empty[Node]
			f(this)
			visited += this
			traversingTree(f)(using visited)
		
		@throws[IllegalArgumentException]
		private infix def setParent (parent: Node): Unit =
			this._parent = Some(parent)
			parent.traverseParent { p =>
				if p == parent then
					throw new IllegalArgumentException(
						s"failed set parent ${parent.langTag.lang} to ${langTag.lang}: " +
						s"Cannot set parent to a child of itself"
					)
					this._parent = None
			}
		
		def detachParent (): Unit =
			this._parent.foreach(_.removeChild(this))
		
		def removeChild (child: Node): Unit =
			child._parent = None
			_children -= child
		
		@throws[IllegalArgumentException]
		infix def addChild (child: Node): Unit =
			val child_old_parent = child._parent
			if child._parent.nonEmpty then
				child.detachParent()
			try child setParent this
			catch case e: IllegalArgumentException =>
				child_old_parent.foreach(_ addChild child)
				throw e
			this._children += child
		
		@throws[IllegalArgumentException]
		infix def addChild (child: LangTag): Node =
			val node = new Node(child)
			addChild(node)
			node
		
		private def printTree (node: Node, prefix: String = "", printer: PrintWriter): Unit = {
			printer.println(s"$prefix${node.langTag}")
			node.children.foreach { child =>
				printTree(child, prefix + "  ", printer)
			}
		}
		
		def printTree: String =
			val s = StringWriter()
			printTree(this, "", PrintWriter(s))
			s.toString
		
		override def toString: String =
			printTree
		
	}
	object Node:
		def defaultRoot: Node = Node(LangTag("root", 0))
	
	val root: Node = Node.defaultRoot
	
	def search (langTag: String): Option[Node] =
		val _langTag = LangTag.normalizeLangTag(langTag)
		boundary {
			root.traverseTree { node =>
				if node.langTag.lang == _langTag then
					boundary.break(Some(node))
			}
			None
		}
	
}

object LanguageTree {
	
	@throws[IllegalArgumentException]
	def parseTreeDocument (document: String): LanguageTree = {
		
		val lines = document.replaceAll("\\r", "").split('\n')
		val tree = new LanguageTree
		val root = tree.root
		import tree.Node
		var currentLevel = mutable.ListBuffer[Node](root)
		
		def countHeadingWhitespaceLevel (line: String)(using whitespaceSize: Int): Option[(Int, String)] =
			val whitespace = line.takeWhile(_.isWhitespace)
			if whitespace.length % whitespaceSize == 0 then
				Some(whitespace.length / whitespaceSize -> line.drop(whitespace.length))
			else None
		
		for (i <- lines.indices) {
			val line = lines(i)
			val line_number = i+1
			countHeadingWhitespaceLevel(line)(using 2) match
				case Some((level, content)) =>
					val langTag: LangTag = content.split(",", 2) match
						case Array(lang) =>
							try LangTag(LangTag.ensureLangTag(LangTag.normalizeLangTag(lang)), 0)
							catch case e: IllegalLangTagException =>
								throw new IllegalArgumentException(
									s"illegal lang name at line $line_number: ${e.getMessage}"
								).initCause(e)
						case Array(lang, priority) =>
							try LangTag(
								LangTag.ensureLangTag(LangTag.normalizeLangTag(lang)),
								priority.filterNot(List(' ', ',', '-', '_', '\'').contains(_)).toInt
							)
							catch
								case e: NumberFormatException =>
									throw new IllegalArgumentException(
										s"failed parse lang's priority at line $line_number: ${e.getMessage}"
									).initCause(e)
								case e: IllegalLangTagException =>
									throw new IllegalArgumentException(
										s"illegal lang name at line $line_number: ${e.getMessage}"
									).initCause(e)
						case _ =>
							throw new IllegalArgumentException(
								s"failed parse at line $line_number: line with invalid format."
							)
					val node = Node(langTag)
					if level < (currentLevel.length + 1) then
						currentLevel = currentLevel take (level + 1)
						currentLevel.last addChild node
						currentLevel += node
					else if level == (currentLevel.length + 1) then
						currentLevel.last addChild node
						currentLevel += node
					else
						throw new IllegalArgumentException(
							s"failed parse at line $line_number: line with invalid indentation."
						)
				case None =>
					throw new IllegalArgumentException(
						s"failed parse at line $line_number: line with invalid indentation."
					)
		}
		
		tree
		
	}
	
}
