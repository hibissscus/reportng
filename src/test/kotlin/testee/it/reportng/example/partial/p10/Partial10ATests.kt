package testee.it.reportng.example.partial.p10

import org.testng.annotations.Test

/**
 * Some successful tests, some not 10%.
 */
@Test(groups = ["partial"])
class Partial10ATests {

    @Test
    fun `successful 01`() {
        Thread.sleep(1000)
        assert(true)
    }

    @Test
    fun `failed 01`() {
        assert(false)
    }

    @Test
    fun `failed 02`() {
        assert(false)
    }

    @Test
    fun `failed 03`() {
        assert(false)
    }

    @Test
    fun `failed 04`() {
        assert(false)
    }

    @Test
    fun `failed 05`() {
        assert(false)
    }

    @Test
    fun `failed 06`() {
        assert(false)
    }

    @Test
    fun `failed 07`() {
        assert(false)
    }

    @Test
    fun `failed 08`() {
        assert(false)
    }

    @Test
    fun `failed 09`() {
        assert(false)
    }
}