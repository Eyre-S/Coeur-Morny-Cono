package cc.sukazyo.cono.morny.test.utils.tgapi.formatting

import cc.sukazyo.cono.morny.test.MornyTests

class TelegramParseEscapeTest extends MornyTests {
	
	"while escape HTML document :" - {
		
		import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
		val any_other = "0ir0Q*%_\"ir[0\"#*I%T\"I{EtjpJGI{\")#W*IT}P%*IH#){#NIJB9-/q{$(Jg'9m]q|MH4j0hq}|+($NR{')}}"
		
		"& must be escaped" in:
				h("a & b") shouldEqual "a &amp; b"
		"< and > must be escaped" in:
				h("<data-error>") shouldEqual "&lt;data-error&gt;"
		"& and < and > must all be escaped" in:
				h("<some-a> && <some-b>") shouldEqual "&lt;some-a&gt; &amp;&amp; &lt;some-b&gt;"
		"space and count should be kept" in:
				h("\t<<<<  \n") shouldEqual "\t&lt;&lt;&lt;&lt;  \n"
		"any others should kept origin like" in:
				h(any_other) shouldEqual any_other
		
	}
	
}
