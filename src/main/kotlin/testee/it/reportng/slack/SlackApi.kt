package testee.it.reportng.slack

import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import testee.it.reportng.slack.model.Msg
import testee.it.reportng.slack.model.Resp
import java.io.File


class SlackApi(val token: String) {

    enum class SlackColor(val color: String) {
        RED("#c0110f"), GREEN("#2eb886"), YELLOW("#ffef28"), GREY("#b4b7b8");
    }

    /**
     * https://api.slack.com/methods/conversations.history/test
     */
    fun getHistory(channel: String, since: String = "1630140851"): Msg? {
        return RestTemplate().getForObject(
                "https://slack.com/api/conversations.history?" +
                        "&token=$token" +
                        "&channel=$channel" +
                        "&limit=100" +
                        "&oldest=$since",
                Msg::class.java)
    }

    /**
     * https://api.slack.com/methods/chat.postMessage
     */
    fun postMessage(channel: String, text: String = "test"): Resp? {
        return RestTemplate().postForObject(
                "https://slack.com/api/chat.postMessage?" +
                        "&token=$token" +
                        "&channel=$channel" +
                        "&text=$text" +
                        "&pretty=1",
                null,
                Resp::class.java)
    }

    /**
     * https://api.slack.com/methods/chat.delete/test
     */
    fun deleteMessage(channel: String, ts: String): Resp? {
        return RestTemplate().postForObject(
                "https://slack.com/api/chat.delete?" +
                        "&token=$token" +
                        "&channel=$channel" +
                        "&ts=$ts" +
                        "&pretty=1",
                null,
                Resp::class.java)
    }

    /**
     * https://api.slack.com/methods/files.upload
     */
    fun postFile(channel: String, title: String, filename: String, file: File): Resp? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        val fileMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        val contentDisposition = ContentDisposition
                .builder("form-data")
                .name("file")
                .filename(filename)
                .build()
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
        val fileEntity = HttpEntity(file.readBytes(), fileMap)
        val body: LinkedMultiValueMap<String, Any> = LinkedMultiValueMap<String, Any>()
        body.add("file", fileEntity)
        body.add("token", token)
        body.add("channels", channel)
        body.add("title", title)
        val requestEntity: HttpEntity<MultiValueMap<String, Any>> = HttpEntity(body, headers)
        try {
            val response: ResponseEntity<Resp> = RestTemplate().exchange(
                    "https://slack.com/api/files.upload",
                    HttpMethod.POST,
                    requestEntity,
                    Resp::class.java)
            return response.body
        } catch (e: HttpClientErrorException) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val slackApi = SlackApi("xoxb-************-*************-************************")
            println(slackApi.postMessage("test", "test"))
            println(slackApi.postFile("C02CWMLT8P9", "e2e results", "e2e.png", File("e2e.png")))
            println(slackApi.postFile("C02CWMLT8P9", "e2e.zip", "e2e.zip", File("e2e.zip")))
        }
    }

}