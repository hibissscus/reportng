package testee.it.reportng

import org.testng.ITestContext
import org.testng.TestListenerAdapter

/**
 * Generating intermediate reports during test execution with multithreading support
 */
class HTMLReporterRuntime : TestListenerAdapter() {

    companion object {
        val htmlReporter = HTMLReporter()
    }

    @Volatile
    private var reportAllowed = true

    private val lock = Object()

    private fun sendToSlack(testContext: ITestContext?) {
        synchronized(lock) {
            if (reportAllowed) {
                reportAllowed = false
                htmlReporter.createHTMLReport(listOf(testContext!!.suite), testContext.outputDirectory)
                reportAllowed = true
            }
        }
    }

    override fun onFinish(testContext: ITestContext?) {
        super.onFinish(testContext)
        sendToSlack(testContext)
    }
}