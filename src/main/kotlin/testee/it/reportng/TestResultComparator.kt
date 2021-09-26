package testee.it.reportng

import org.testng.ITestResult

/**
 * Comparator for sorting TestNG test results alphabetically by method name.
 */
internal class TestResultComparator : Comparator<ITestResult> {
    override fun compare(result1: ITestResult, result2: ITestResult): Int {
        return result1.name.compareTo(result2.name)
    }
}