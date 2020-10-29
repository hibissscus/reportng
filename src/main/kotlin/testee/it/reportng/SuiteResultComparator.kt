package testee.it.reportng

import org.testng.ISuiteResult
import java.util.Comparator

/**
 * Comparator for sorting TestNG test results by passRate and alphabetically by method name.
 */
internal class SuiteResultComparator : Comparator<ISuiteResult> {
    override fun compare(result1: ISuiteResult, result2: ISuiteResult): Int {
        val rate1 = rate(result1)
        val rate2 = rate(result2)
        if (rate1 == rate2) {
            return result1.testContext.name.compareTo(result2.testContext.name)
        }
        return if (rate1 > rate2) 1 else -1
    }

    private fun rate(result: ISuiteResult): Double {
        return if (result.testContext.passedTests.size() <= 0) {
            0.0
        } else {
            val passedTests = result.testContext.passedTests.size().toDouble()
            val failedTests = result.testContext.failedTests.size().toDouble()
            val skippedTests = result.testContext.skippedTests.size().toDouble()
            passedTests / (passedTests + failedTests + skippedTests) * 100
        }
    }
}