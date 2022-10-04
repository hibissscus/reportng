package testee.it.reportng.example.failed

import org.testng.Reporter
import org.testng.annotations.Test

/**
 * Failed tests.
 */
@Test(groups = ["failed"])
class FailedTests {

    @Test
    fun assertionFailure() {
        assert(false) { "This test failed." }
    }

    @Test
    fun assertionFailureWithMultilineMessage() {
        assert(false) { "This test failed.\nIts message is on multiple lines.\n     The last one has leading whitespace." }
    }

    @Test
    fun assertionFailureWithOutput() {
        Reporter.log("Here is some output from an unsuccessful test.")
        assert(false) { "This test failed." }
    }

    @Test
    fun exceptionThrown() {
        throw IllegalStateException(
            "Test failed.",
            UnsupportedOperationException()
        ) // Nested cause.
    }
}