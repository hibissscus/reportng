package testee.it.reportng.slack.model

data class Message(
    var text: String? = null,
    var username: String? = null,
    var bot_id: String? = null,
    var attachments: List<Attachment> = ArrayList(),
    var type: String? = null,
    var subtype: String? = null,
    var ts: String? = null
)