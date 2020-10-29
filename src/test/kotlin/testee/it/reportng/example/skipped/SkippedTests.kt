package testee.it.reportng.example.skipped

import org.testng.SkipException
import org.testng.annotations.Test

/**
 * Skipped tests 0%.
 */
@Test(groups = ["skipped"])
class SkippedTests {

    @Test
    fun skipped() {
        throw SkipException("Skipping this test")
    }
}