import sbt.*

//noinspection TypeAnnotation
object MornyConfiguration {
	
	val MORNY_ARCHIVE_NAME = "morny-coeur"
	
	val MORNY_CODE_STORE = "https://github.com/Eyre-S/Coeur-Morny-Cono"
	val MORNY_COMMIT_PATH = "https://github.com/Eyre-S/Coeur-Morny-Cono/commit/%s"
	
	val VERSION = "2.0.0-alpha5"
	val VERSION_DELTA: Option[String] = None
	val CODENAME = "guanggu"
	
	val SNAPSHOT = true
	
	val dependencies = Seq(
		
		"com.github.spotbugs" % "spotbugs-annotations" % "4.7.3" % Compile,
		
		"cc.sukazyo" % "messiva" % "0.2.0",
		"cc.sukazyo" % "resource-tools" % "0.2.2",
		
		"com.github.pengrad" % "java-telegram-bot-api" % "6.2.0",
		
		"com.softwaremill.sttp.client3" %% "core" % "3.9.0",
		"com.softwaremill.sttp.client3" %% "okhttp-backend" % "3.9.0",
		"com.squareup.okhttp3" % "okhttp" % "4.11.0" % Runtime,
		
		"com.google.code.gson" % "gson" % "2.10.1",
		"io.circe" %% "circe-core" % "0.14.6",
		"io.circe" %% "circe-generic" % "0.14.6",
		"io.circe" %% "circe-parser" % "0.14.6",
		"org.jsoup" % "jsoup" % "1.16.2",
		
		"com.cronutils" % "cron-utils" % "9.2.0",
		
		// used for disable slf4j
		// due to the slf4j api have been used in the following libraries:
		//  - cron-utils
		"org.slf4j" % "slf4j-nop" % "2.0.9" % Runtime,
		
		"org.scalatest" %% "scalatest" % "3.2.17" % Test,
		"org.scalatest" %% "scalatest-freespec" % "3.2.17" % Test,
		// for test report
		"com.vladsch.flexmark" % "flexmark" % "0.64.0" % Test,
		"com.vladsch.flexmark" % "flexmark-profile-pegdown" % "0.64.0" % Test
		
	)
	
	val publishTo: Some[Resolver] = {
		//noinspection SimplifyBooleanMatch
		SNAPSHOT match {
			case true => Some("-ws-snapshots" at "https://mvn.sukazyo.cc/snapshots")
			case false => Some("-ws-releases" at "https://mvn.sukazyo.cc/releases")
		}
//		Some(Resolver.file("build", file("S:/__tests/artifacts")))
//		None
	}
	val publishCredentials: Seq[Credentials] = {
		Seq(Credentials(Path.userHome / ".sbt" / ("workshop-mvn"+".credentials")))
//		Nil
	}
	
}
