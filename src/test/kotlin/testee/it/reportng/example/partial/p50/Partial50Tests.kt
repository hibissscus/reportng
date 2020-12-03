package testee.it.reportng.example.partial.p50

import org.testng.annotations.Test

/**
 * Some successful tests, some not 50%.
 */
@Test(groups = ["partial"])
class Partial50Tests {

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
    fun `successful 05`() {
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
}