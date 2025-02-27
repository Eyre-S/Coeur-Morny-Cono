package cc.sukazyo.cono.morny.system

import cc.sukazyo.restools.{ResourceDirectory, ResourcePackage}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

trait MornySystemTests
	extends AnyFreeSpec
		with Matchers
		with TableDrivenPropertyChecks {
	
	object Assets {
		val pack: ResourcePackage = ResourcePackage.get("assets/morny-system/tests")
		val root: ResourceDirectory = pack.getDirectory("assets/morny-system/tests")
	}
	
	object Sttp {
		import sttp.client3.{basicRequest, Empty, RequestT}
		import sttp.model.{Header, HeaderNames}
		
		val testingBasicRequest: RequestT[Empty, Either[String, String], Any] =
			basicRequest
				.header(Header(HeaderNames.UserAgent, s"MornySystem / 0.0.0-tests"), true)
		
	}
	
}
