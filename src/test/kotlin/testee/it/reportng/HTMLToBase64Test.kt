package testee.it.reportng

import org.testng.annotations.Test
import java.io.IOException

class HTMLToBase64Test {

    @Test
    @Throws(IOException::class)
    fun testHTMLToBase64() {
        assert(HTMLToBase64.htmlToBase64(
                HTMLToBase64.html, 1024, 768, "e2e.png").isNotEmpty()) { "Wrong html to base64 encoding" }
    }
}