package testee.it.reportng

import org.testng.ISuiteResult
import java.util.Comparator

/**
 * Comparator for sorting TestNG test results by passRate and alphabetically by method name.
 */
internal class SuiteResultComparator : Comparator<Map.Entry<String?, ISuiteResult>> {
    override fun compare(o1: Map.Entry<String?, ISuiteResult>,
                         o2: Map.Entry<String?, ISuiteResult>): Int {
        val rate1 = rate(o1.value)
        val rate2 = rate(o2.value)
        if (rate1 == rate2) {
            return o1.value.testContext.name.compareTo(o2.value.testContext.name)
        }
        return if (rate1 > rate2) -1 else 1
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