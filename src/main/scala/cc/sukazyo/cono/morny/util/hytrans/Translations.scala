package cc.sukazyo.cono.morny.util.hytrans

import cc.sukazyo.cono.morny.util.var_text.{Var, VarText}

import scala.util.boundary

class Translations (
	langs: LanguageTree,
	translations: Map[String, Definitions]
) {
	
	def traverse (using lang: String)(f: (LangTag, Definitions) => Unit): Unit = {
		langs.search(lang)
			.getOrElse(langs.root)
			.traverseTree(node =>
				translations.get(node.langTag.lang)
					.map(
						f(node.langTag, _)
					)
			)
	}
	
	def traverseWithKey (key: String)(using lang: String)(f: (LangTag, Option[String]) => Unit): Unit = {
		this.traverse (using lang) { (langTag, definitions) =>
			f(langTag, definitions.get(key))
		}
	}
	
	def get (key: String)(using lang: String): Option[String] = {
		boundary {
			traverseWithKey(key) { (_, value) =>
				if value.nonEmpty then
					boundary.break(Some(value.get))
			}
			None
		}
	}
	
	def trans (key: String)(using lang: String): VarText =
		VarText(get(key).getOrElse(s"#[$key@$lang]"))
	
	def trans (key: String, args: Var*)(using lang: String): String =
		trans(key).render(args*)
	
}
