package testee.it.reportng.example.partial.p80

import org.testng.annotations.Test

/**
 * Some successful tests, some not 80%.
 */
@Test(groups = ["partial"])
class Partial80Tests {

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
    fun `successful 08`() {
        Thread.sleep(8000)
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
}