package testee.it.reportng.example.allkind

import org.testng.SkipException
import org.testng.annotations.Listeners
import org.testng.annotations.Test
import testee.it.reportng.HTMLReporter

/**
 * Some successful tests, some not, some skipped 33%.
 */
@Test(groups = ["allkind"])
@Listeners(HTMLReporter::class)
class AllKindTests {

    @Test
    fun successful() {
        assert(true)
    }

    @Test
    fun skipped() {
        throw SkipException("Skipping this test")
    }

    @Test
    fun failed() {
        assert(false)
    }
}