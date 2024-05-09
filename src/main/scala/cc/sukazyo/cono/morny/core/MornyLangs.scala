package cc.sukazyo.cono.morny.core

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyLangs.load
import cc.sukazyo.cono.morny.data.MornyAssets
import cc.sukazyo.cono.morny.util.hytrans.*
import cc.sukazyo.cono.morny.util.UseThrowable.toLogString
import cc.sukazyo.cono.morny.util.var_text.{Var, VarText}

import java.io.IOException
import scala.collection.mutable
import scala.util.boundary
import scala.util.boundary.break

object MornyLangs {
	
	private def load(): Translations = {
		
		val (lang_index, lang_trans) = {
			
			logger `info` s"Loading Morny's translation data."
			
			val (lang_dir, lang_index_content) = try {
				(
					MornyAssets.pack.getResDir("langs"),
					MornyAssets.pack.getResource("langs/_index.hyl").readAsString()
				)
			} catch case e: IOException =>
				throw Exception("Cannot read Morny's translations file.", e)
			val my_index = LanguageTree.parseTreeDocument(lang_index_content)
			val indexed_langs: Set[String] = {
				val lang_tags = mutable.ListBuffer.empty[String]
				my_index.root.traverseTree(lang_tags += _.langTag.lang)
				logger `info` s"indexed following languages: ${lang_tags.mkString(", ")}"
				lang_tags.toSet
			}
			
			val language_translations = mutable.HashMap.empty[String, Definitions]
			
			for (file <- lang_dir.listFiles().filter(_.isFile)) yield {
				boundary {
					import file.getPath as raw_path
					if !(raw_path.endsWith(".hyt") || raw_path.endsWith(".hytrans")) then break()
					val file_name = file.getPath.reverse.takeWhile(c => (c != '/') && (c != '\\')).reverse
					val file_basename = file_name.dropRight(
						if file_name.endsWith(".hyt") then ".hyt".length
						else ".hytrans".length
					)
					val normalized = LangTag.normalizeLangTag(file_basename)
					if !indexed_langs.contains(normalized) then
						logger `warn` s"translation file \"$file_name\" is not in language index, so it got ignored (normalized lang name is \"$normalized\")."
						break()
					val lang_def = try {
						val content = file.readAsString()
						Parser.parse(content)
					} catch case e: IOException =>
						logger `error`
							s"""Failed read/parse translation file $file_name (normalized lang name is $normalized):
							   |${e.toLogString.indent(2)}
							   |due to failed, this ($file_name) has been ignored.""".stripMargin
						break()
					logger `info` s"read language file $file_name (normalized lang name is $normalized), with ${lang_def.size} entries."
					if language_translations contains normalized then
						// TODO: merge
						logger `warn` s"  language $normalized seems already loaded one yet, this will override the old one!"
					else language_translations += (normalized -> lang_def)
				}
			}
			
			(my_index, language_translations.toMap)
			
		}
		
		Translations(lang_index, lang_trans)
		
	}
	
}

class MornyLangs {
	
	private var translations: Translations = load()
	
	def reload (): Unit =
		translations = load()
	
	def getRaw: Translations = translations
	
	def traverse (using lang: String)(f: (LangTag, Definitions) => Unit): Unit =
		getRaw.traverse(f)
	def traverseWithKey (key: String)(using lang: String)(f: (LangTag, Option[String]) => Unit): Unit =
		getRaw.traverseWithKey(key)(f)
	def get (key: String)(using lang: String): Option[String] =
		getRaw.get(key)
	def trans (key: String)(using lang: String): VarText =
		getRaw.trans(key)
	def trans (key: String, args: Var*)(using lang: String): String =
		getRaw.trans(key, args*)
	def transAsVar (key: String, args: Var*)(using lang: String): Var =
		getRaw.transAsVar(key, args*)
	
}
