package testee.it.reportng

import org.junit.Ignore
import org.junit.Test

class ImageToBase64Test {

    @Ignore
    @Test
    fun testImageToBase64() {
        assert(
            ImageToBase64.encodeToString(
                ImageToBase64.decodeToImage(ImageToBase64.base64),
                "png"
            ) == ImageToBase64.base64
        ) { "Wrong base64 encoding" }
    }
}