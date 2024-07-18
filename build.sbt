aether.AetherKeys.aetherOldVersionMethod := true

ThisBuild / organization := MornyProject.group
ThisBuild / organizationName := MornyProject.group_name

ThisBuild / version := MornyProject.version

ThisBuild / scalaVersion := "3.4.1"

ThisBuild / resolvers ++= Seq(
		"-ws-releases" at "https://mvn.sukazyo.cc/releases"
)

ThisBuild / crossPaths := false

ThisBuild / Compile / packageDoc / publishArtifact := false
artifactName := {(sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
	val classifier = artifact.classifier match {
		case Some(value) => s"-$value"
		case None => ""
	}
	s"${module.name}-${module.revision}$classifier.${artifact.extension}"
}

ThisBuild / scalacOptions ++= Seq(
	"-language:postfixOps",
	"-encoding", MornyProject.source_encoding
)
ThisBuild / javacOptions ++= Seq(
	"-encoding", MornyProject.source_encoding,
	"-source", "17",
	"-target", "17"
)
ThisBuild / autoAPIMappings := true
ThisBuild / apiMappings ++= {
	def mappingsFor(organization: String, names: List[String], location: String, revision: String => String = identity): Seq[(File, URL)] =
		for {
			entry: Attributed[File] <- (Compile / fullClasspath).value
			module: ModuleID <- entry.get(moduleID.key)
			if module.organization == organization
			if names.exists(module.name.startsWith)
		} yield entry.data -> url(location.format(revision(module.revision)))
	val mappings: Seq[(File, URL)] = Seq(
		mappingsFor("org.scala-lang", List("scala-library"), "https://scala-lang.org/api/%s/"),
		mappingsFor("com.github.pengrad", "java-telegram-bot-api"::Nil, "https://jitpack.io/com/github/pengrad/java-telegram-bot-api/6.3.0/javadoc/"),
	).flatten
	mappings.toMap
}

ThisBuild / publishTo := MornyProject.publishTo
ThisBuild / credentials ++= MornyProject.publishCredentials

lazy val morny_system_lib = (project in file (MornyProject.morny_system_lib.id))
		.enablePlugins(BuildInfoPlugin)
		.settings(
			
			name := MornyProject.morny_system_lib.name,
			moduleName := MornyProject.morny_system_lib.id,
			
			libraryDependencies ++= MornyProject.morny_system_lib.dependencies,
			
		)

lazy val morny_coeur = (project in file(MornyProject.morny_coeur.id))
		.enablePlugins(BuildInfoPlugin)
		.dependsOn(morny_system_lib)
		.settings(
			
			name := MornyProject.morny_coeur.name,
			moduleName := MornyProject.morny_coeur.id,
			
			Compile / mainClass := Some(MornyProject.morny_coeur.main_class),
			
			libraryDependencies ++= MornyProject.morny_coeur.dependencies,
			
			buildInfoPackage := MornyProject.morny_coeur.root_package,
			buildInfoObject := "BuildConfig",
			buildInfoKeys ++= Seq(
				BuildInfoKey[String]("VERSION", MornyProject.version),
				BuildInfoKey[String]("VERSION_FULL", MornyProject.version_full),
				BuildInfoKey[String]("VERSION_BASE", MornyProject.version_base),
				BuildInfoKey[Option[String]]("VERSION_DELTA", MornyProject.version_delta),
				BuildInfoKey[String]("CODENAME", MornyProject.version_codename),
				BuildInfoKey.action[Long]("CODE_TIMESTAMP") { MornyProject.code_time },
				BuildInfoKey.action[String]("COMMIT") { MornyProject.git_commit },
				BuildInfoKey.action[Boolean]("CLEAN_BUILD") { MornyProject.git_is_clean },
				BuildInfoKey[String]("CODE_STORE", MornyProject.git_store),
				BuildInfoKey[String]("COMMIT_PATH", MornyProject.git_store_path),
			),
			
			assemblyMergeStrategy := {
				case module if module endsWith "module-info.class" => MergeStrategy.concat
				case module_kt if module_kt endsWith ".kotlin_module" => MergeStrategy.concat
				case version if (version startsWith "META-INF") && (version endsWith ".versions.properties") => MergeStrategy.concat
				case x =>
					val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
					oldStrategy(x)
			},
			assembly / artifact := (assembly / artifact).value
					.withClassifier(Some("fat")),
			if (MornyProject.publishWithFatJar) {
				addArtifact(assembly / artifact, assembly)
			} else Nil,
			if (System.getenv("DOCKER_BUILD") != null) {
				assembly / assemblyJarName := {
					sLog.value info "environment DOCKER_BUILD checked"
					sLog.value info "assembly will output for docker build (morny-coeur-docker-build.jar)"
					s"${MornyProject.morny_coeur.id}-docker-build.jar"
				}
			} else Nil,
			
		)

lazy val root = (project in file ("."))
		.aggregate(morny_system_lib, morny_coeur)
		.settings(
			assembly / aggregate := false,
			assembly := {
				(morny_coeur / assembly).value
			},
			run := {
				(morny_coeur / Compile / run).evaluated
			}
		)
