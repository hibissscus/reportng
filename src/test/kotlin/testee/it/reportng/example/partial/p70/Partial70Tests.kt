package testee.it.reportng.example.partial.p70

import org.testng.annotations.Test

/**
 * Some successful tests, some not 70%.
 */
@Test(groups = ["partial"])
class Partial70Tests {

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
    fun `successful 06`() {
        assert(true)
    }

    @Test
    fun `successful 07`() {
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
}