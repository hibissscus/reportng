package testee.it.reportng.slack.model

data class File(
    var id: String? = null,
    var created: String? = null,
    var timestamp: String? = null,
    var name: String? = null,
    var title: String? = null,
    var mimetype: String? = null,
    var filetype: String? = null,
    var pretty_type: String? = null,
    var user: String? = null,
    var size: String? = null,
    var original_w: String? = null,
    var original_h: String? = null,
)