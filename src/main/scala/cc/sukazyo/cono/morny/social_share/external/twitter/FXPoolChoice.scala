package cc.sukazyo.cono.morny.social_share.external.twitter

/** Data for a single choice in a poll
  *
  * @param label What this choice in the poll is called
  * @param count How many people voted in this poll
  * @param percentage Percentage of total people who voted for this option (0 - 100, rounded to nearest tenth)
  */
case class FXPoolChoice (
	label: String,
	count: Int,
	percentage: Int
)
