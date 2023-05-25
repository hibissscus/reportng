package testee.it.reportng.example.skipped

import org.testng.SkipException
import org.testng.annotations.Listeners
import org.testng.annotations.Test
import testee.it.reportng.HTMLReporter

/**
 * Skipped tests 0%.
 */
@Test(groups = ["skipped"])
@Listeners(HTMLReporter::class)
class SkippedTests {

    @Test
    fun skipped() {
        throw SkipException("Skipping this test")
    }
}