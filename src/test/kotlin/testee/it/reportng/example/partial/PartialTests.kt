package testee.it.reportng.example.partial

import org.testng.SkipException
import org.testng.annotations.Test

/**
 * Some successful tests, some not, some skipped 50%.
 */
@Test(groups = ["partial"])
class PartialTests {

    @Test
    fun successful() {
        assert(true)
    }

    @Test
    fun successful2() {
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