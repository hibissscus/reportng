package testee.it.reportng.slack.model


data class Msg(
        var ok: Boolean = false,
        var has_more: Boolean = false,
        var pin_count: Int = 0,
        var channel_actions_ts: String? = null,
        var channel_actions_count: Int = 0,
        var messages: List<Message> = ArrayList()
)