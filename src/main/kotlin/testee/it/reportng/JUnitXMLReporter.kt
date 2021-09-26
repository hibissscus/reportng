package testee.it.reportng

import java.io.File
import java.util.LinkedList
import org.testng.IClass
import org.testng.ISuite
import org.testng.ITestResult
import org.testng.xml.XmlSuite

/**
 * JUnit XML reporter for TestNG that uses Velocity templates to generate its
 * output.
 */
class JUnitXMLReporter : AbstractReporter(TEMPLATES_PATH) {

    companion object {
        private const val TEMPLATES_PATH = "testee/it/reportng/templates/xml/"
        private const val REPORT_DIRECTORY = "xml"
        private const val RESULTS_FILE = "results.xml"
        private const val RESULTS_KEY = "results"
    }

    /**
     * Generates a set of XML files (JUnit format) that contain data about the
     * outcome of the specified test suites.
     *
     * @param suites              Data about the test runs.
     * @param outputDirectoryName The directory in which to create the report.
     */
    override fun generateReport(xmlSuites: List<XmlSuite>,
                                suites: List<ISuite>,
                                outputDirectoryName: String) {
        removeEmptyDirectories(File(outputDirectoryName))
        val outputDirectory = File(outputDirectoryName, REPORT_DIRECTORY)
        outputDirectory.mkdirs()
        val flattenedResults = flattenResults(suites)
        for (results in flattenedResults) {
            val context = createContext()
            context.put(RESULTS_KEY, results)
            try {
                generateFile(File(outputDirectory, results.testClass.name + '_' + RESULTS_FILE),
                        RESULTS_FILE + TEMPLATE_EXTENSION,
                        context)
            } catch (ex: Exception) {
                throw ReportNGException("Failed generating JUnit XML report.", ex)
            }
        }
    }

    /**
     * Flatten a list of test suite results into a collection of results grouped by test class.
     * This method basically strips away the TestNG way of organising tests and arranges
     * the results by test class.
     */
    private fun flattenResults(suites: List<ISuite>): Collection<TestClassResults> {
        val flattenedResults: MutableMap<IClass, TestClassResults> = HashMap()
        for (suite in suites) {
            for (suiteResult in suite.results.values) {
                // Failed and skipped configuration methods are treated as test failures.
                organiseByClass(suiteResult.testContext.failedConfigurations.allResults, flattenedResults)
                organiseByClass(suiteResult.testContext.skippedConfigurations.allResults, flattenedResults)
                // Successful configuration methods are not included.
                organiseByClass(suiteResult.testContext.failedTests.allResults, flattenedResults)
                organiseByClass(suiteResult.testContext.skippedTests.allResults, flattenedResults)
                organiseByClass(suiteResult.testContext.passedTests.allResults, flattenedResults)
            }
        }
        return flattenedResults.values
    }

    private fun organiseByClass(testResults: Set<ITestResult>,
                                flattenedResults: MutableMap<IClass, TestClassResults>) {
        for (testResult in testResults) {
            getResultsForClass(flattenedResults, testResult).addResult(testResult)
        }
    }

    /**
     * Look-up the results data for a particular test class.
     */
    private fun getResultsForClass(flattenedResults: MutableMap<IClass, TestClassResults>,
                                   testResult: ITestResult): TestClassResults {
        var resultsForClass = flattenedResults[testResult.testClass]
        if (resultsForClass == null) {
            resultsForClass = TestClassResults(testResult.testClass)
            flattenedResults[testResult.testClass] = resultsForClass
        }
        return resultsForClass
    }

    /**
     * Groups together all of the data about the tests results from the methods
     * of a single test class.
     */
    class TestClassResults(val testClass: IClass) {
        private val failedTests: MutableCollection<ITestResult> = LinkedList()
        private val skippedTests: MutableCollection<ITestResult> = LinkedList()
        private val passedTests: MutableCollection<ITestResult> = LinkedList()
        var duration: Long = 0
            private set

        /**
         * Adds a test result for this class.  Organises results by outcome.
         */
        fun addResult(result: ITestResult) {
            when (result.status) {
                ITestResult.SKIP -> {
                    run {
                        if (META.allowSkippedTestsInXML()) {
                            skippedTests.add(result)
                            return
                        }
                    }
                    run { failedTests.add(result) }
                }
                ITestResult.FAILURE, ITestResult.SUCCESS_PERCENTAGE_FAILURE -> {
                    failedTests.add(result)
                }
                ITestResult.SUCCESS -> {
                    passedTests.add(result)
                }
            }
            duration += result.endMillis - result.startMillis
        }

        fun getFailedTests(): Collection<ITestResult> {
            return failedTests
        }

        fun getSkippedTests(): Collection<ITestResult> {
            return skippedTests
        }

        fun getPassedTests(): Collection<ITestResult> {
            return passedTests
        }
    }
}