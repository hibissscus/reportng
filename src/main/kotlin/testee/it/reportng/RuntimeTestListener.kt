package testee.it.reportng

import org.testng.ITestContext
import org.testng.TestListenerAdapter

class RuntimeTestListener : TestListenerAdapter() {

    companion object {
        val htmlReporter = HTMLReporter()
    }

    override fun onFinish(testContext: ITestContext?) {
        super.onFinish(testContext)
        htmlReporter.createHTMLReport(listOf(testContext!!.suite), testContext.outputDirectory, false)
    }
}