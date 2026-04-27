package cc.sukazyo.cono.morny.extra.twitter

/** Information about the author of a tweet.
  *
  * @param name Name of the user, set on their profile
  * @param screen_name Screen name or @ handle of the user.
  * @param avatar_url URL for the user's avatar (profile picture)
  * @param avatar_color Palette color corresponding to the user's avatar (profile picture). Value is a hex, including `#`.
  * @param banner_url URL for the banner of the user
  */
case class FXAuthor (
	name: String,
	url: String,
	screen_name: String,
	avatar_url: Option[String],
	avatar_color: Option[String],
	banner_url: Option[String],
	description: Option[String], // todo
	location: Option[String], // todo
	website: Option[FXAuthor.websiteType], // todo
	followers: Option[Int], // todo
	following: Option[Int], // todo
	joined: Option[String], // todo
	likes: Option[Int], // todo
	tweets: Option[Int] // todo
)

object FXAuthor {
	case class websiteType (
		url: String,
		display_url: String
	)
}
