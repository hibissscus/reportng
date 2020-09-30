package testee.it.reportng;

import org.junit.Ignore;

import static testee.it.reportng.ImageToBase64.base64;
import static testee.it.reportng.ImageToBase64.decodeToImage;
import static testee.it.reportng.ImageToBase64.encodeToString;

public class ImageToBase64Test {
    @Ignore
    public void testImageToBase64() {
        assert encodeToString(decodeToImage(base64), "png").equals(base64) : "Wrong base64 encoding";
    }
}