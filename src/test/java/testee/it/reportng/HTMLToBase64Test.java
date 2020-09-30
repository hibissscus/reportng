package testee.it.reportng;

import org.testng.annotations.Test;

import java.io.IOException;

public class HTMLToBase64Test {
    @Test
    public void testHTMLToBase64() throws IOException {
        assert HTMLToBase64.htmlToBase64(
                HTMLToBase64.html, 1024, 768, "e2e.png").length() > 0 :
                "Wrong html to base64 encoding";
    }
}