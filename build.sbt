aether.AetherKeys.aetherOldVersionMethod := true

ThisBuild / organization := "cc.sukazyo"
ThisBuild / organizationName := "A.C. Sukazyo Eyre"

ThisBuild / scalaVersion := "3.3.1"

resolvers ++= Seq(
		"-ws-releases" at "https://mvn.sukazyo.cc/releases"
)

lazy val root = (project in file("."))
		.enablePlugins(BuildInfoPlugin)
		.settings(
			
			name := "Coeur Morny Cono",
			version := MornyProject.version,
			
			crossPaths := false,
			moduleName := MornyProject.app_archive_name,
			Compile / packageDoc / publishArtifact := false,
			artifactName := {(sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
				val classifier = artifact.classifier match {
					case Some(value) => s"-$value"
					case None => ""
				}
				s"${module.name}-${MornyProject.version_full}$classifier.${artifact.extension}"
			},
			
			Compile / mainClass := Some(MornyProject.app_application_main),
			
			libraryDependencies ++= MornyProject.dependencies,
			
			buildInfoPackage := MornyProject.app_package,
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
			
			scalacOptions ++= Seq(
				"-language:postfixOps",
				"-encoding", MornyProject.source_encoding
			),
			javacOptions ++= Seq(
				"-encoding", MornyProject.source_encoding,
				"-source", "17",
				"-target", "17"
			),
			
			assemblyMergeStrategy := {
				case module if module endsWith "module-info.class" => MergeStrategy.concat
				case module_kt if module_kt endsWith ".kotlin_module" => MergeStrategy.concat
				case x =>
					val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
					oldStrategy(x)
			},
			assembly / artifact := (assembly / artifact).value
					.withClassifier(Some("fat")),
			addArtifact(assembly / artifact, assembly),
			if (System.getenv("DOCKER_BUILD") != null) {
				assembly / assemblyJarName := {
					sLog.value info "environment DOCKER_BUILD checked"
					sLog.value info "assembly will output for docker build (morny-coeur-docker-build.jar)"
					"morny-coeur-docker-build.jar"
				}
			} else Nil,
			
			publishTo := MornyProject.publishTo,
			credentials ++= MornyProject.publishCredentials,
			
		)
