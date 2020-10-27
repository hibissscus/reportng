package testee.it.reportng.example.allkind;

import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Some successful tests, some not, some skipped 33%.
 */
@Test(groups = "allkind")
public class AllkindTests {

    @Test
    public void successful() {
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
