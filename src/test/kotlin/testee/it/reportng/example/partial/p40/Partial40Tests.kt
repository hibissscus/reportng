package testee.it.reportng.example.partial.p40

import org.testng.annotations.Test

/**
 * Some successful tests, some not 40%.
 */
@Test(groups = ["partial"])
class Partial40Tests {

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
        assert(true)
    }

    @Test
    fun `successful 04`() {
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
}