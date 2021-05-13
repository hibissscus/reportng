package testee.it.reportng.example.partial.p30

import org.testng.annotations.Test

/**
 * Some successful tests, some not 30%.
 */
@Test(groups = ["partial"])
class Partial30Tests {

    @Test
    fun `successful 01`() {
        assert(true)
    }

    @Test
    fun `successful 02`() {
        assert(true)
    }

    @Test
    fun `successful 03`() {
        Thread.sleep(3000)
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
}