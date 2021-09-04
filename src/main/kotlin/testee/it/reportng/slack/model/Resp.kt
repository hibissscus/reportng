package testee.it.reportng.slack.model

data class Resp(
        var ok: Boolean = false,
        var channel: String? = null,
        var ts: String? = null,
        var message: Message? = null,
        var file: File? = null
)