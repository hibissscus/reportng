package testee.it.reportng

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.IClass
import org.testng.IResultMap
import org.testng.ISuite
import org.testng.ISuiteResult
import org.testng.ITestNGMethod
import org.testng.ITestResult
import org.testng.Reporter
import org.testng.xml.XmlSuite
import testee.it.reportng.HTMLToBase64.htmlToBase64
import testee.it.reportng.ZipUtils.zip
import testee.it.reportng.slack.SlackApi
import testee.it.reportng.slack.model.Resp
import java.io.File
import java.io.IOException
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.absolutePathString
import kotlin.math.abs

/**
 * Enhanced HTML reporter for TestNG that uses Velocity templates to generate its
 * output.
 */
class HTMLReporter : AbstractReporter(TEMPLATES_PATH) {

    var logger: Logger = LoggerFactory.getLogger(HTMLReporter::class.java)

    companion object {
        private var slackImage: Resp? = null
        private var slackZip: Resp? = null

        private const val FRAMES_PROPERTY = "testee.it.reportng.frames"
        private const val ONLY_FAILURES_PROPERTY = "testee.it.reportng.failures-only"

        private const val TEMPLATES_PATH = "testee/it/reportng/templates/html/"
        private const val INDEX_FILE = "index.html"
        private const val SUITES_FILE = "suites.html"
        private const val OVERVIEW_FILE = "overview.html"
        private const val GROUPS_FILE = "groups.html"
        private const val RESULTS_FILE = "results.html"
        private const val OUTPUT_FILE = "output.html"
        private const val CUSTOM_STYLE_FILE = "custom.css"
        private const val RESULT_IMAGE_FILE = "e2e.png"
        private const val RESULT_ZIP_FILE = "e2e.zip"

        private const val SUITE_KEY = "suite"
        private const val SUITES_KEY = "suites"
        private const val GROUPS_KEY = "groups"
        private const val RESULT_KEY = "result"
        private const val FAILED_CONFIG_KEY = "failedConfigurations"
        private const val SKIPPED_CONFIG_KEY = "skippedConfigurations"
        private const val FAILED_TESTS_KEY = "failedTests"
        private const val SKIPPED_TESTS_KEY = "skippedTests"
        private const val PASSED_TESTS_KEY = "passedTests"
        private const val ONLY_FAILURES_KEY = "onlyReportFailures"

        private const val REPORT_DIRECTORY = "e2e"
        private const val REPORT_DIRECTORY_IMAGES = "images"

        private val METHOD_COMPARATOR: Comparator<ITestNGMethod> = TestMethodComparator()
        private val RESULT_COMPARATOR: Comparator<ITestResult> = TestResultComparator()
        private val CLASS_COMPARATOR: Comparator<IClass> = TestClassComparator()
        private val SUITE_RESULT_COMPARATOR = SuiteResultComparator()

        private fun sortByValue(unsortedMap: Map<String, ISuiteResult>): Map<String, ISuiteResult> {
            // 1. Convert Map to List of Map
            val linkedList = LinkedList(unsortedMap.entries)

            // 2. Sort list with Collections.sort(), provide a custom Comparator
            //    Try switch the o1 o2 position for a different order
            linkedList.sortWith(SUITE_RESULT_COMPARATOR)

            // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
            val sortedMap: MutableMap<String, ISuiteResult> = LinkedHashMap()
            for ((key, value) in linkedList) {
                sortedMap[key] = value
            }
            return sortedMap
        }
    }

    /**
     * Generates a set of HTML files that contain data about the outcome of
     * the specified test suites.
     *
     * @param suites              Data about the test runs.
     * @param outputDirectoryName The directory in which to create the report.
     */
    override fun generateReport(
        xmlSuites: List<XmlSuite>,  // not used
        suites: List<ISuite>,
        outputDirectoryName: String
    ) {
        createHTMLReport(suites, outputDirectoryName)
    }

    fun createHTMLReport(
        suites: List<ISuite>,
        outputDirectoryName: String
    ) {
        removeEmptyDirectories(File(outputDirectoryName))
        val useFrames = System.getProperty(FRAMES_PROPERTY, "true") == "true"
        val onlyFailures = System.getProperty(ONLY_FAILURES_PROPERTY, "false") == "true"
        val outputDirectory = File(outputDirectoryName, REPORT_DIRECTORY)
        outputDirectory.mkdirs()

        //  Sorting TestNG test results by passRate from 100% to 0% and alphabetically by method name.
        for (suite in suites) {
            val results = sortByValue(suite.results)
            suite.results.clear()
            suite.results.putAll(results)
        }
        try {
            if (useFrames) {
                createFrameset(outputDirectory)
            }
            createOverview(suites, outputDirectory, !useFrames, onlyFailures)
            createSuiteList(suites, outputDirectory, onlyFailures)
            createGroups(suites, outputDirectory)
            createResults(suites, outputDirectory, onlyFailures)
            createLog(outputDirectory, onlyFailures)
            copyResources(outputDirectory)
            createBase64Overview(outputDirectory)
            createSlackNotification(outputDirectory)
            println(
                "See test report at " + URI.create(
                    "file:" + File.separator + File.separator + Paths.get(outputDirectory.path, INDEX_FILE)
                        .absolutePathString()
                )
            )
        } catch (ex: Exception) {
            throw ReportNGException("Failed generating HTML report.", ex)
        }
    }

    /**
     * Create base64 representation of overview.html.
     *
     * @param outputDirectory where overview.html e2e.png is stored.
     */
    private fun createBase64Overview(outputDirectory: File) {
        try {
            val resultFilePath = File(outputDirectory, RESULT_IMAGE_FILE).toPath().toString()
            // create base64 representation of overview.html
            htmlToBase64(
                String(Files.readAllBytes(File(outputDirectory, OVERVIEW_FILE).toPath())),
                1024, 768,
                resultFilePath
            )
        } catch (e: IOException) {
            throw ReportNGException("Failed to create base64 representation of overview.html", e)
        }
    }


    /**
     *  Zip file generation out of test result report.
     *
     * @param outputDirectory where overview.html e2e.png is stored.
     */
    private fun createZipArchiveFile(outputDirectory: File): File? {
        if (META.allowZipArchive()) {
            // delete all zip archives
            Files.deleteIfExists(Paths.get(outputDirectory.path, RESULT_ZIP_FILE))
            /*
            // delete all images before zipping
            val imagesPath = Paths.get(outputDirectory.path, REPORT_DIRECTORY_IMAGES)
            if (Files.exists(imagesPath)) {
                Files.walk(imagesPath)
                    .sorted()
                    .map { obj: Path -> obj.toFile() }
                    .forEach { obj: File -> obj.delete() }
            }
            val e2ePath = Paths.get(outputDirectory.path, REPORT_DIRECTORY)
            if (Files.exists(e2ePath)) {
                Files.walk(e2ePath)
                    .sorted()
                    .map { obj: Path -> obj.toFile() }
                    .forEach { obj: File -> obj.delete() }
            }*/
            return zip(outputDirectory.path, "e2e")
        } else return null
    }


    /**
     * Send base64 representation of overview.html to Slack channel if it's enabled.
     *
     * @param outputDirectory where overview.html e2e.png is stored.
     */
    private fun createSlackNotification(outputDirectory: File) {
        try {
            if (META.allowSlackNotification()) {
                val zipFile = createZipArchiveFile(outputDirectory)
                val imageFile = File(outputDirectory, RESULT_IMAGE_FILE)
                val slack = SlackApi(META.getSlackToken()!!)
                if (slackImage?.file?.id != null) {
                    slack.deleteFile(slackImage?.file?.id!!)
                }
                slackImage = slack.postFile(META.getSlackChannel()!!, "e2e results", RESULT_IMAGE_FILE, imageFile)
                if (zipFile != null) {
                    if (slackZip?.file?.id != null) {
                        slack.deleteFile(slackZip?.file?.id!!)
                    }
                    slackZip = slack.postFile(META.getSlackChannel()!!, RESULT_ZIP_FILE, RESULT_ZIP_FILE, zipFile)
                }
            }
        } catch (e: Exception) {
            throw ReportNGException("Failed to send slack test result notification.", e)
        }
    }

    /**
     * Create the index file that sets up the frameset.
     *
     * @param outputDirectory The target directory for the generated file(s).
     */
    @Throws(Exception::class)
    private fun createFrameset(outputDirectory: File) {
        val context = createContext()
        generateFile(
            File(outputDirectory, INDEX_FILE),
            INDEX_FILE + TEMPLATE_EXTENSION,
            context
        )
    }

    @Throws(Exception::class)
    private fun createOverview(
        suites: List<ISuite>,
        outputDirectory: File,
        isIndex: Boolean,
        onlyFailures: Boolean
    ) {
        val context = createContext()
        context.put(SUITES_KEY, suites)
        context.put(ONLY_FAILURES_KEY, onlyFailures)
        generateFile(
            File(outputDirectory, if (isIndex) INDEX_FILE else OVERVIEW_FILE),
            OVERVIEW_FILE + TEMPLATE_EXTENSION,
            context
        )
    }

    /**
     * Create the navigation frame.
     *
     * @param outputDirectory The target directory for the generated file(s).
     */
    @Throws(Exception::class)
    private fun createSuiteList(
        suites: List<ISuite>,
        outputDirectory: File,
        onlyFailures: Boolean
    ) {
        val context = createContext()
        context.put(SUITES_KEY, suites)
        context.put(ONLY_FAILURES_KEY, onlyFailures)
        generateFile(
            File(outputDirectory, SUITES_FILE),
            SUITES_FILE + TEMPLATE_EXTENSION,
            context
        )
    }

    /**
     * Generate a results file for each test in each suite.
     *
     * @param outputDirectory The target directory for the generated file(s).
     */
    @Throws(Exception::class)
    private fun createResults(
        suites: List<ISuite>,
        outputDirectory: File,
        onlyShowFailures: Boolean
    ) {
        var index = 1
        for (suite in suites) {
            var index2 = 1
            for (result in suite.results.values) {
                val failuresExist = (result.testContext.failedTests.size() > 0
                        || result.testContext.failedConfigurations.size() > 0)
                if (!onlyShowFailures || failuresExist) {
                    val context = createContext()
                    context.put(RESULT_KEY, result)
                    context.put(FAILED_CONFIG_KEY, sortByTestClass(result.testContext.failedConfigurations))
                    context.put(SKIPPED_CONFIG_KEY, sortByTestClass(result.testContext.skippedConfigurations))
                    context.put(FAILED_TESTS_KEY, sortByTestClass(result.testContext.failedTests))
                    context.put(SKIPPED_TESTS_KEY, sortByTestClass(result.testContext.skippedTests))
                    context.put(PASSED_TESTS_KEY, sortByTestClass(result.testContext.passedTests))

                    // BYPASS USING THE CONTEXT
                    // the generateFile method uses  Velocity.mergeTemplate( context is passed in here
                    // Note context.put here is passing in a key and IResultMap (
                    // For this to work with screenshots you need to modify the testng framework ( TestRunner class ? )
                    // to have a similar method for getScreenShots that returns an IResultMap and also a method to add a screenshot
                    // conclusion. too complex / too much code to modify ( i.e. not only this class but also the testng framework ).
                    // see http://testng.org/doc/javadocs/org/testng/TestRunner.html
                    val fileName = String.format("suite%d_test%d_%s", index, index2, RESULTS_FILE)
                    generateFile(
                        File(outputDirectory, fileName),
                        RESULTS_FILE + TEMPLATE_EXTENSION,
                        context
                    )
                }
                ++index2
            }
            ++index
        }
    }

    /**
     * Group test methods by class and sort alphabetically.
     */
    private fun sortByTestClass(results: IResultMap): SortedMap<IClass, List<ITestResult>>? {
        val sortedResults: SortedMap<IClass, List<ITestResult>> = TreeMap(CLASS_COMPARATOR)
        for (result in results.allResults) {
            val resultsForClass: ArrayList<ITestResult> =
                sortedResults.computeIfAbsent(result.testClass) { ArrayList() } as ArrayList<ITestResult>
            var index = Collections.binarySearch(resultsForClass, result, RESULT_COMPARATOR)
            if (index < 0) {
                index = abs(index + 1)
            }
            resultsForClass.add(index, result)
        }
        return sortedResults
    }

    /**
     * Generate a groups list for each suite.
     *
     * @param outputDirectory The target directory for the generated file(s).
     */
    @Throws(Exception::class)
    private fun createGroups(
        suites: List<ISuite>,
        outputDirectory: File
    ) {
        var index = 1
        for (suite in suites) {
            val groups = sortGroups(suite.methodsByGroups)
            if (!groups.isEmpty()) {
                val context = createContext()
                context.put(SUITE_KEY, suite)
                context.put(GROUPS_KEY, groups)
                val fileName = String.format("suite%d_%s", index, GROUPS_FILE)
                generateFile(
                    File(outputDirectory, fileName),
                    GROUPS_FILE + TEMPLATE_EXTENSION,
                    context
                )
            }
            ++index
        }
    }

    /**
     * Generate a groups list for each suite.
     *
     * @param outputDirectory The target directory for the generated file(s).
     */
    @Throws(Exception::class)
    private fun createLog(outputDirectory: File, onlyFailures: Boolean) {
        if (!Reporter.getOutput().isEmpty()) {
            val context = createContext()
            context.put(ONLY_FAILURES_KEY, onlyFailures)
            generateFile(
                File(outputDirectory, OUTPUT_FILE),
                OUTPUT_FILE + TEMPLATE_EXTENSION,
                context
            )
        }
    }

    /**
     * Sorts groups alphabetically and also sorts methods within groups alphabetically
     * (class name first, then method name).  Also eliminates duplicate entries.
     */
    private fun sortGroups(groups: Map<String, Collection<ITestNGMethod>>): SortedMap<String, SortedSet<ITestNGMethod>> {
        val sortedGroups: SortedMap<String, SortedSet<ITestNGMethod>> = TreeMap()
        for ((key, value) in groups) {
            val methods: SortedSet<ITestNGMethod> = TreeSet(METHOD_COMPARATOR)
            methods.addAll(value)
            sortedGroups[key] = methods
        }
        return sortedGroups
    }

    /**
     * Reads the CSS and JavaScript files from the JAR file and writes them to
     * the output directory.
     *
     * @param outputDirectory Where to put the resources.
     * @throws IOException If the resources can't be read or written.
     */
    @Throws(IOException::class)
    private fun copyResources(outputDirectory: File) {
        copyClasspathResource(outputDirectory, "reportng.css", "reportng.css")
        copyClasspathResource(outputDirectory, "reportng.js", "reportng.js")
        copyClasspathResource(outputDirectory, "reveal.css", "reveal.css")
        copyClasspathResource(outputDirectory, "reveal.js", "reveal.js")
        // If there is a custom stylesheet, copy that.
        val customStylesheet = META.getStylesheetPath()
        if (customStylesheet != null) {
            if (customStylesheet.exists()) {
                copyFile(outputDirectory, customStylesheet, CUSTOM_STYLE_FILE)
            } else {
                // If not found, try to read the file as a resource on the classpath
                // useful when reportng is called by a jarred up library
                val stream = ClassLoader.getSystemClassLoader().getResourceAsStream(customStylesheet.path)
                if (stream != null) {
                    copyStream(outputDirectory, stream, CUSTOM_STYLE_FILE)
                }
            }
        }
    }
}