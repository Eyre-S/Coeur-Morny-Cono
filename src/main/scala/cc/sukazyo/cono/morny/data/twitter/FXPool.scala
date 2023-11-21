package cc.sukazyo.cono.morny.data.twitter

/** Data for a poll on a given Tweet.
  *
  * @param choices Array of the poll choices
  * @param total_votes Total votes in poll
  * @param ends_at Date of which the poll ends
  * @param time_left_en Time remaining counter in English (i.e. **9 hours left**)
  */
case class FXPool (
	choices: List[FXPoolChoice],
	total_votes: Int,
	ends_at: String,
	time_left_en: String
)
