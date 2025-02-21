package cc.sukazyo.cono.morny

trait IBuildInfo {
	
	val name          : String
	val version       : String
	val scalaVersion  : String
	val sbtVersion    : String
	val VERSION       : String
	val VERSION_FULL  : String
	val VERSION_BASE  : String
	val VERSION_DELTA : Option[String]
	val CODENAME      : String
	val CODE_TIMESTAMP: Long
	val COMMIT        : String
	val CLEAN_BUILD   : Boolean
	val CODE_STORE    : String
	val COMMIT_PATH   : String
	
}
