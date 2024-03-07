package cc.sukazyo.cono.morny.test.utils.var_text

import cc.sukazyo.cono.morny.test.MornyTests
import cc.sukazyo.cono.morny.util.var_text.{VarText, VTNodeLiteral, VTNodeVar}

class VarTextTest extends MornyTests {
	
	"VarText template convertor works." in {
		VarText("abcdefg {one_var}{following}it /{escaped}it and this is //double-escape-literal, with a /no-need-to-escape then {{non formatted}}xxx {missing_part")
			.toString
			.shouldEqual(VarText(
				VTNodeLiteral("abcdefg "),
				VTNodeVar("one_var"),
				VTNodeVar("following"),
				VTNodeLiteral("it {escaped}it and this is /double-escape-literal, with a /no-need-to-escape then "),
				VTNodeLiteral("{{non formatted}}xxx "),
				VTNodeLiteral("{missing_part"),
			).toString)
	}
	
}
