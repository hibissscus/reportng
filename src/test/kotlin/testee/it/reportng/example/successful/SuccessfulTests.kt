package testee.it.reportng.example.successful

import org.testng.Reporter
import org.testng.annotations.Test

/**
 * Successful tests.
 */
@Test(groups = ["successful"])
class SuccessfulTests {

    @Test
    fun test() {
        assert(true)
    }

    @Test(description = "This is a test description")
    fun testWithDescription() {
        assert(true)
    }

    @Test
    fun testWithOutput() {
        Reporter.log("Here is some output from a successful test")
        assert(true)
    }

    @Test
    fun testWithMultiLineOutput() {
        Reporter.log("This is the first line of 3")
        Reporter.log("This is a second line")
        Reporter.log("This is the third")
        assert(true)
    }
}