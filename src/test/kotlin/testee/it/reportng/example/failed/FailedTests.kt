package testee.it.reportng.example.failed

import org.testng.Reporter
import org.testng.annotations.Listeners
import org.testng.annotations.Test
import testee.it.reportng.HTMLReporter

/**
 * Failed tests.
 */
@Test(groups = ["failed"])
@Listeners(HTMLReporter::class)
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