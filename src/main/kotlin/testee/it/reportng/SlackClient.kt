package testee.it.reportng

import com.squareup.okhttp.OkHttpClient
import org.json.JSONObject
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.client.OkClient
import retrofit.http.Multipart
import retrofit.http.POST
import retrofit.http.Part
import retrofit.mime.TypedFile
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object SlackClient {

    enum class SlackColor(val color: String) {
        RED("#c0110f"), GREEN("#2eb886"), YELLOW("#ffef28"), GREY("#b4b7b8");
    }

    interface Slack {
        @Multipart
        @POST("/files.upload")
        fun sendFile(
                @Part("token") token: String,
                @Part("channels") channels: String,
                @Part("file") file: TypedFile,
                @Part("title") title: String,
                @Part("filename") filename: String?,
                callback: Callback<JSONObject?>
        )

        @Multipart
        @POST("/chat.postMessage")
        fun sendMessage(
                @Part("token") token: String,
                @Part("channel") channel: String,
                @Part("text") text: String,
                @Part("as_user") as_user: String,
                @Part("attachments") attachments: String,
                callback: Callback<JSONObject?>
        )
    }

    /**
     * Initialize slack http client
     */
    @JvmStatic
    fun initialize(): Slack {
        // create client
        val okHttpClient = OkHttpClient()
        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS)
        okHttpClient.setWriteTimeout(10, TimeUnit.SECONDS)
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS)

        //slack
        val slackRestAdapter = RestAdapter.Builder()
                .setEndpoint("https://slack.com/api")
                .setClient(OkClient(okHttpClient))
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build()
        return slackRestAdapter.create(Slack::class.java)
    }

    @JvmStatic
    fun sendTestReportImageToSlack(
            slack: Slack,
            slackToken: String,
            channel: String,
            testResultImage: File) {
        uploadFile(
                slack,
                slackToken,
                channel,
                testResultImage,
                "e2e results",
                null)
    }

    @JvmStatic
    fun sendTestReportZipToSlack(
            slack: Slack,
            slackToken: String,
            channel: String,
            testResultZip: File) {
        uploadFile(
                slack,
                slackToken,
                channel,
                testResultZip,
                "e2e.zip",
                "e2e.zip")
    }

    private fun getEmptyCallback(): Callback<JSONObject?> {
        return object : Callback<JSONObject?> {
            override fun failure(p0: RetrofitError?) {
                println("failure")
            }

            override fun success(p0: JSONObject?, p1: retrofit.client.Response?) {
                println("success")
            }
        }
    }

    /**
     * Upload Screenshot to slack channel
     */
    fun uploadFile(
            slack: Slack,
            slackToken: String,
            channel: String,
            file: File,
            title: String,
            filename: String?) {
        slack.sendFile(
                slackToken,
                channel,
                TypedFile("multipart/form-data", file),
                title,
                filename,
                getEmptyCallback())
    }

    /**
     * Send status message to slack channel
     */
    fun sendMessage(
            slack: Slack,
            token: String,
            channel: String,
            text: String,
            color: SlackColor,
            link: String?,
            asUser: Boolean) {
        slack.sendMessage(
                token,
                channel,
                "",
                asUser.toString().toLowerCase(),
                "[{\"color\": \"" + color.color + "\", \"title\": \"" + text + "\", \"title_link\": \"" + link + "\"}]",
                getEmptyCallback())
    }


    @Throws(IOException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        println("start")

        val slackToken = "xoxb-2523321089-496303117776-prfRkxRcT1fBTphL3HwvcjfK"
        val slack = initialize()
        val channel = "esp8266"
        sendMessage(slack, slackToken, channel, "test result", SlackColor.GREEN, null, true)
        sendTestReportImageToSlack(slack, slackToken, channel, File("e2e.png"))
        Thread.sleep(5000L)
        sendTestReportZipToSlack(slack, slackToken, channel, File("e2e_reports.zip"))
        println("done")
    }

}