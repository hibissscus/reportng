package testee.it.reportng

import org.testng.ITestResult

/**
 * Comparator for sorting TestNG test results by passing order.
 */
internal class TestResultComparator : Comparator<ITestResult> {
    override fun compare(result1: ITestResult, result2: ITestResult): Int {
        var compare = result1.startMillis.compareTo(result2.startMillis)
        if (compare == 0) {
            compare = result1.endMillis.compareTo(result2.endMillis)
        }
        return compare
    }
}