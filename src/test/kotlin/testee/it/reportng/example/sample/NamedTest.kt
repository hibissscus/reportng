package testee.it.reportng.example.sample

import org.testng.ITest
import org.testng.annotations.Test

/**
 * Test for named tests.
 */
class NamedTest : ITest {
    override fun getTestName(): String {
        return "NamedTest"
    }

    @Test
    fun testNamed() {
        // Do nothing, will pass.
    }
}