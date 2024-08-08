import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import sbt.*

//noinspection TypeAnnotation
object MornyProject {
	
	val _git_repo = new FileRepositoryBuilder()
		.setGitDir(file(".git"))
		.setWorkTree(file("."))
		.readEnvironment()
		.build()
	val _git = new Git(_git_repo)
	def _git_head = _git_repo.resolve("HEAD")
	def _git_head_commit = new RevWalk(_git_repo).parseCommit(_git_head)
	val git_exists = true
	val git_store = MornyConfiguration.MORNY_CODE_STORE
	def git_commit = _git_head_commit.getName
	def git_commit_time = _git_head_commit.getCommitTime*1000L
	val git_store_path = MornyConfiguration.MORNY_COMMIT_PATH
	// todo: is clean for project should only take care of app src and build src
	def git_is_clean = _git.status.call.isClean
	
	// todo: git status log
	
//	val app_group = "cc.sukazyo"
//	val app_package = s"$app_group.cono.morny"
//	val app_application_main = s"$app_package.core.ServerMain"
	
	val version_base = MornyConfiguration.VERSION
	val version_delta = MornyConfiguration.VERSION_DELTA
	val version_is_snapshot = MornyConfiguration.SNAPSHOT
	val version = Seq(
		version_base,
		version_delta match { case Some(value) => "δ" + value case None => null},
		if (version_is_snapshot) "SNAPSHOT" else null
	).filterNot(f => f == null).mkString("-")
	def version_full = version + (if (!git_exists) "" else
		s"+git${git_commit take 8}${if (git_is_clean) "" else ".δ"}")
	val version_codename = MornyConfiguration.CODENAME
	def code_time = if (git_is_clean) git_commit_time else System.currentTimeMillis
	
	val group = MornyConfiguration.GROUP
	val group_name = MornyConfiguration.GROUP_NAME
	
	val docker_image_name = MornyConfiguration.DOCKER_IMAGE_NAME
	
	val publishWithFatJar = !version_is_snapshot
	val publishWithDocJar = !version_is_snapshot
	def publishTo = MornyConfiguration.publishTo
	val publishCredentials = MornyConfiguration.publishCredentials
	
	val source_encoding = "utf-8"
	
	val morny_system_lib = MornyConfiguration.Morny_System_Library
	val morny_coeur = MornyConfiguration.Morny_Coeur
	
}
