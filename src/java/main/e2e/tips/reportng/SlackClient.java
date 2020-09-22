package e2e.tips.reportng;

import com.squareup.okhttp.OkHttpClient;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SlackClient {

    public enum StatusColor {
        RED("#c0110f"),
        GREEN("#2eb886"),
        YELLOW("#ffef28"),
        GREY("#b4b7b8");
        public final String color;

        StatusColor(final String color) {
            this.color = color;
        }
    }

    /**
     * Initialize slack http client
     */
    public static Slack initialize() {
        // create client
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(1, TimeUnit.MINUTES);
        okHttpClient.setWriteTimeout(1, TimeUnit.MINUTES);
        okHttpClient.setConnectTimeout(1, TimeUnit.MINUTES);

        //slack
        RestAdapter slackRestAdapter = new RestAdapter.Builder()
                .setEndpoint("https://slack.com/api")
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return slackRestAdapter.create(Slack.class);
    }

    /**
     * Upload Screenshot to slack channel
     */
    public static void uploadFile(
            Slack slack,
            String slackToken,
            String channel,
            File file,
            String filename,
            String title) {

        slack.sendFile(
                slackToken,
                channel,
                new TypedFile("multipart/form-data", file),
                filename,
                title,
                null);
    }

    /**
     * Send status message to slack channel
     */
    public static void sendMessage(
            Slack slack,
            String token,
            String channel, String text,
            StatusColor color,
            String link,
            Boolean asUser) {
        slack.sendMessage(
                token,
                channel,
                "",
                asUser.toString().toLowerCase(),
                ("[{\"color\": \"" + color.color + "\", \"title\": \"" + text + "\", \"title_link\": \"" + link + "\"}]"),
                null);
    }

    interface Slack {
        @Multipart
        @POST("/files.upload")
        void sendFile(
                @Part("token") String token,
                @Part("channels") String channels,
                @Part("file") TypedFile file,
                @Part("filename") String filename,
                @Part("title") String title,
                Callback<JSONObject> callback
        );

        @Multipart
        @POST("/chat.postMessage")
        void sendMessage(
                @Part("token") String token,
                @Part("channel") String channel,
                @Part("text") String text,
                @Part("as_user") String as_user,
                @Part("attachments") String attachments,
                Callback<JSONObject> callback
        );
    }


    public static void sendTestReportImageToSlack(
            Slack slack,
            String slackToken,
            String channel,
            File testResultImage) {

        uploadFile(
                slack,
                slackToken,
                channel,
                testResultImage,
                null,
                "e2e results");
    }

    public static void sendTestReportZipToSlack(
            Slack slack,
            String slackToken,
            String channel,
            File testResultZip) {

        uploadFile(
                slack,
                slackToken,
                channel,
                testResultZip,
                "e2e.zip",
                "e2e.zip");

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final String slackToken = "****-2523321089-496303117776-************************";
        final Slack slack = initialize();
        final String channel = "*****66";

        sendTestReportImageToSlack(slack, slackToken, channel, new File("e2e.png"));
        Thread.sleep(10000L);
        sendTestReportZipToSlack(slack, slackToken, channel, new File("e2e_reports.zip"));
        Thread.sleep(5000L);
    }
}