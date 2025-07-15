package testee.it.reportng.slack

import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatDeleteRequest
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest
import com.slack.api.methods.request.files.FilesDeleteRequest
import com.slack.api.methods.request.files.FilesUploadV2Request
import com.slack.api.methods.request.files.FilesUploadV2Request.UploadFile
import testee.it.reportng.slack.model.Attachment
import testee.it.reportng.slack.model.Message
import testee.it.reportng.slack.model.Msg
import testee.it.reportng.slack.model.Resp
import java.io.File

class SlackApi(token: String) {

    enum class SlackColor(val color: String) {
        RED("#c0110f"), GREEN("#2eb886"), YELLOW("#ffef28"), GREY("#b4b7b8");
    }

    private val slack = Slack.getInstance()
    private val methods = slack.methods(token)

    /**
     * https://api.slack.com/methods/conversations.history/test
     */
    fun getHistory(channel: String, since: String = "1630140851"): Msg? {
        val request = ConversationsHistoryRequest.builder()
            .channel(channel)
            .limit(100)
            .oldest(since)
            .build()

        val response = methods.conversationsHistory(request)

        if (!response.isOk) {
            println("Error getting history: ${response.error}")
            return null
        }

        // Convert from slack-api-client model to our model
        val messages = response.messages.map { msg ->
            Message(
                text = msg.text,
                username = msg.username,
                bot_id = msg.botId,
                attachments = msg.attachments?.map { att ->
                    Attachment(
                        text = att.text,
                        id = att.id?.toInt(),
                        fallback = att.fallback
                    )
                } ?: emptyList(),
                type = msg.type,
                subtype = msg.subtype,
                ts = msg.ts
            )
        }

        return Msg(
            ok = response.isOk,
            has_more = response.isHasMore,
            messages = messages
        )
    }

    /**
     * https://api.slack.com/methods/chat.postMessage
     */
    fun postMessage(channel: String, text: String = "test"): Resp? {
        val request = ChatPostMessageRequest.builder()
            .channel(channel)
            .text(text)
            .build()

        val response = methods.chatPostMessage(request)

        if (!response.isOk) {
            println("Error posting message: ${response.error}")
            return null
        }

        // Convert from slack-api-client model to our model
        val message = response.message?.let { msg ->
            Message(
                text = msg.text,
                username = msg.username,
                bot_id = msg.botId,
                type = msg.type,
                subtype = msg.subtype,
                ts = msg.ts
            )
        }

        return Resp(
            ok = response.isOk,
            channel = response.channel,
            ts = response.ts,
            message = message
        )
    }

    /**
     * https://api.slack.com/methods/chat.delete/test
     */
    fun deleteMessage(channel: String, ts: String): Resp? {
        val request = ChatDeleteRequest.builder()
            .channel(channel)
            .ts(ts)
            .build()

        val response = methods.chatDelete(request)

        if (!response.isOk) {
            println("Error deleting message: ${response.error}")
            return null
        }

        return Resp(
            ok = response.isOk,
            channel = response.channel,
            ts = response.ts
        )
    }

    /**
     * https://slack.com/api/files.delete/test
     */
    fun deleteFile(file: String): Resp? {
        val request = FilesDeleteRequest.builder()
            .file(file)
            .build()

        val response = methods.filesDelete(request)

        if (!response.isOk) {
            println("Error deleting file: ${response.error}")
            return null
        }

        return Resp(
            ok = response.isOk
        )
    }

    /**
     * Resolves a Slack channel ID from its name (e.g., "#general" → "C12345678").
     *
     * This function uses the `conversations.list` API to find a public or visible private channel
     * by its name. The bot must have `channels:read` and/or `groups:read` scopes depending on
     * channel visibility. The bot must also be a member of the private channel if applicable.
     *
     * @param name the Slack channel name (e.g., "#general" or "general")
     * @return the channel ID as a string (e.g., "C12345678"), or `null` if not found
     */
    fun getChannelIdByName(name: String): String? {
        val response = methods.conversationsList { it.excludeArchived(true).limit(1000) }

        if (!response.isOk) {
            println("Error fetching channels: ${response.error}")
            return null
        }

        val targetName = name.removePrefix("#") // Remove '#' if provided

        return response.channels?.firstOrNull { it.name == targetName }?.id
    }

    /**
     * Upload a file to Slack using the filesUploadV2 API
     */
    fun postFile(channel: String, title: String, filename: String, file: File): Resp? {
        try {
            val uploadFile = UploadFile.builder()
                .file(file)
                .filename(filename)
                .title(title)
                .build()

            val request = FilesUploadV2Request.builder()
                .channel(getChannelIdByName(channel))
                .uploadFiles(listOf(uploadFile))
                .build()

            val response = methods.filesUploadV2(request)

            if (!response.isOk) {
                println("Error uploading file: ${response.error}")
                return null
            }

            val f = response.files?.firstOrNull()
            val slackFile = f?.let {
                testee.it.reportng.slack.model.File(
                    id = it.id,
                    created = it.created?.toString(),
                    timestamp = it.timestamp?.toString(),
                    name = it.name,
                    title = it.title,
                    mimetype = it.mimetype,
                    filetype = it.filetype,
                    pretty_type = it.prettyType,
                    user = it.user,
                    size = it.size?.toString(),
                    original_w = it.originalWidth,
                    original_h = it.originalHeight
                )
            }

            return Resp(
                ok = response.isOk,
                file = slackFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    companion object {
        fun main(args: Array<String>) {
            val slackApi = SlackApi("xoxb-************-*************-************************")
            println(slackApi.postMessage("test", "test"))
            println(slackApi.postFile("test", "e2e results", "e2e.png", File("e2e.png")))
            println(slackApi.postFile("test", "e2e.zip", "e2e.zip", File("e2e.zip")))
        }
    }
}