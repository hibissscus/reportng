package testee.it.reportng.example.dependant

import org.testng.annotations.Listeners
import org.testng.annotations.Test
import testee.it.reportng.HTMLReporter

/**
 * Dependant tests 40%.
 */
@Test(groups = ["dependant"])
@Listeners(HTMLReporter::class)
class DependantTests {

    @Test(dependsOnMethods = ["e", "d", "c", "b"])
    fun a() {
        assert(false)
    }

    @Test(dependsOnMethods = ["e", "d", "c"])
    fun b() {
        assert(false)
    }

    @Test(dependsOnMethods = ["e", "d"])
    fun c() {
        assert(false)
    }

    @Test(dependsOnMethods = ["e"])
    fun d() {
        assert(true)
    }

    @Test
    fun e() {
        assert(true)
    }

}