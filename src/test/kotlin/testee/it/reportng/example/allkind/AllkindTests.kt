package testee.it.reportng.example.allkind

import org.testng.SkipException
import org.testng.annotations.Test

/**
 * Some successful tests, some not, some skipped 33%.
 */
@Test(groups = ["allkind"])
class AllkindTests {

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