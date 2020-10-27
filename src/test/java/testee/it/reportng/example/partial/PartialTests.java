package testee.it.reportng.example.partial;

import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Some successful tests, some not, some skipped 50%.
 */
@Test(groups = "partial")
public class PartialTests {

    @Test
    public void successful() {
        assert true;
    }

    @Test
    public void successful2() {
        assert true;
    }

    @Test
    public void skipped() {
        throw new SkipException("Skipping this test");
    }

    @Test
    public void failed() {
        assert false;
    }

}
