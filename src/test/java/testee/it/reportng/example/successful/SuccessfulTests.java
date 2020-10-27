package testee.it.reportng.example.successful;

import org.testng.Reporter;
import org.testng.annotations.Test;

/**
 * Successful tests.
 */
@Test(groups = "successful")
public class SuccessfulTests {

    @Test
    public void test() {
        assert true;
    }


    @Test(description = "This is a test description")
    public void testWithDescription() {
        assert true;
    }


    @Test
    public void testWithOutput() {
        Reporter.log("Here is some output from a successful test");
        assert true;
    }


    @Test
    public void testWithMultiLineOutput() {
        Reporter.log("This is the first line of 3");
        Reporter.log("This is a second line");
        Reporter.log("This is the third");
        assert true;
    }

}
