package e2e.tips.reportng;

import org.testng.annotations.Test;

import static e2e.tips.reportng.ImageToBase64.base64;
import static e2e.tips.reportng.ImageToBase64.decodeToImage;
import static e2e.tips.reportng.ImageToBase64.encodeToString;

public class ImageToBase64Test {
    @Test
    public void testImageToBase64() {
        assert encodeToString(decodeToImage(base64), "png").equals(base64) : "Wrong base64 encoding";
    }
}