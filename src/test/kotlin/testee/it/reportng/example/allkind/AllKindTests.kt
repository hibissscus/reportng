package testee.it.reportng.example.allkind

import org.testng.Reporter
import org.testng.SkipException
import org.testng.annotations.Listeners
import org.testng.annotations.Test
import testee.it.reportng.HTMLReporter

/**
 * Some successful tests, some not, some skipped 33%.
 */
@Test(groups = ["allkind"])
@Listeners(HTMLReporter::class)
class AllKindTests {

    @Test
    fun successful() {
        Reporter.log("This is the first line of 3")
        Reporter.log("This is a second line")
        Reporter.log("This is the third")
        Reporter.log("Loooooooooo oooooooooooooooooo oooooooooooooooooooooo ooooooooooooong")
        assert(true)
    }

    @Test
    fun skipped() {
        Reporter.log("This is the first line of 3")
        Reporter.log("This is a second line")
        Reporter.log("This is the third")
        Reporter.log("Loooooooooo oooooooooooooooooo oooooooooooooooooooooo ooooooooooooong")
        throw SkipException("Skipping this test")
    }

    @Test
    fun failed() {
        Reporter.log("click on action: list.myList.list.action.newOrganizationList")
        Reporter.log("This is a second line")
        Reporter.log("This is the third")
        Reporter.log("Loooooooooo oooooooooooooooooo oooooooooooooooooooooo ooooooooooooong")
        assert(false)
    }
}