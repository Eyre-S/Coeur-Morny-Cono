import sbt.*
import MornyConfiguration.Dependencies.defineModules

import scala.language.implicitConversions

//noinspection TypeAnnotation
object MornyConfiguration {
	
	val MORNY_CODE_STORE = "https://github.com/Eyre-S/Coeur-Morny-Cono"
	val MORNY_COMMIT_PATH = "https://github.com/Eyre-S/Coeur-Morny-Cono/commit/%s"
	
	val VERSION = "2.0.0-alpha21"
	val VERSION_DELTA: Option[String] = None
	val CODENAME = "xinzheng"
	
	val SNAPSHOT = true
	
	val GROUP = "cc.sukazyo"
	val GROUP_NAME = "A.C. Sukazyo Eyre"
	
	val DOCKER_IMAGE_NAME = "coeur-morny-cono"
	
	trait ProjectMetadata {
		
		val name: String
		val id: String
		
		val group: String
		val root_package: String
		
		val dependencies: Seq[ModuleID]
		
	}
	trait Runnable {
		val main_class: String
	}
	
	object Dependencies {
		
		trait ModuleDefinition {
			def compile: Seq[ModuleID]
		}
		class ModuleUnit (val mod: ModuleID) extends ModuleDefinition {
			override def compile: Seq[ModuleID] = Seq(mod)
		}
		class ModuleGroup (val mods: Seq[ModuleDefinition]) extends ModuleDefinition {
			override def compile: Seq[ModuleID] = mods.flatMap(_.compile)
		}
		implicit def moduleToModuleUnit (mod: ModuleID): ModuleDefinition = {
			new ModuleUnit(mod)
		}
		def defineModules (mods: ModuleDefinition*): ModuleDefinition = {
			new ModuleGroup(mods)
		}
		
		val base = defineModules(
			
			"com.github.spotbugs" % "spotbugs-annotations" % "4.9.3" % Compile,
			
			"cc.sukazyo" % "messiva" % "0.2.0",
			"cc.sukazyo" % "da4a" % "0.2.0-SNAPSHOT" changing(),
			
			"com.github.pengrad" % "java-telegram-bot-api" % "6.2.0",
			
		)
		
		val res = defineModules("cc.sukazyo" % "resource-tools" % "0.3.1")
		
		val http4s = defineModules(
			"org.http4s" %% "http4s-dsl" % "0.23.30",
			"org.http4s" %% "http4s-circe" % "0.23.30",
			"org.http4s" %% "http4s-netty-server" % "0.5.23"
		)
		
		val sttp = defineModules(
			"com.softwaremill.sttp.client3" %% "core" % "3.11.0",
			"com.softwaremill.sttp.client3" %% "okhttp-backend" % "3.11.0",
			"com.squareup.okhttp3" % "okhttp" % "4.12.0" % Runtime
		)
		
		val circe = defineModules(
			"io.circe" %% "circe-core" % "0.14.13",
			"io.circe" %% "circe-generic" % "0.14.13",
			"io.circe" %% "circe-parser" % "0.14.13"
		)
		
		val gson = defineModules("com.google.code.gson" % "gson" % "2.13.0")
		
		val jsoup = defineModules("org.jsoup" % "jsoup" % "1.19.1")
		
		val cron_utils = defineModules(
			"com.cronutils" % "cron-utils" % "9.2.1",
			// for disable slf4j
			// due to the slf4j api have been used in the following libraries:
			//  - cron-utils
			"org.slf4j" % "slf4j-nop" % "2.0.17" % Runtime
		)
		
		val test_cases = defineModules(
			"org.scalatest" %% "scalatest" % "3.2.19" % Test,
			"org.scalatest" %% "scalatest-freespec" % "3.2.19" % Test,
			// for test report
			"com.vladsch.flexmark" % "flexmark" % "0.64.8" % Test,
			"com.vladsch.flexmark" % "flexmark-profile-pegdown" % "0.64.8" % Test
		)
		
	}
	
	object Morny_System_Library extends ProjectMetadata {
		
		override val name = "Morny System Library"
		override val id = "morny-system-lib"
		
		override val group = GROUP
		override val root_package = s"${this.group}.cono.morny.system"
		
		override val dependencies = defineModules(
			
			Dependencies.base,
			
			Dependencies.sttp,
			Dependencies.jsoup,
			
			"cc.sukazyo" % "resource-tools" % "0.3.2-SNAPSHOT" % Test changing(),
			Dependencies.test_cases
			
		).compile
		
	}
	
	object Morny_Core extends ProjectMetadata {
		
		override val name = "Morny Core"
		override val id = "morny-core"
		
		override val group = GROUP
		override val root_package = s"$GROUP.cono.morny.core"
		
		override val dependencies = defineModules(
			
			Dependencies.base,
			Dependencies.res,
			
			Dependencies.http4s,
			
			Dependencies.circe,
			Dependencies.cron_utils,
			
			Dependencies.test_cases
			
		).compile
		
	}
	
	object Morny_Coeur extends ProjectMetadata with Runnable {
		
		override val name = "Morny Coeur"
		override val id = "morny-coeur"
		
		override val group = GROUP
		override val root_package = s"$GROUP.cono.morny"
		override val main_class = s"${this.root_package}.core.ServerMain"
		
		override val dependencies = defineModules(
			
			Dependencies.base,
			Dependencies.res,
			Dependencies.http4s,
			
			Dependencies.sttp,
			Dependencies.gson,
			Dependencies.circe,
			Dependencies.jsoup,
			Dependencies.cron_utils,
			
			Dependencies.test_cases
			
		).compile
		
	}
	
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
