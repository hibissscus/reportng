package testee.it.reportng

import org.testng.IInvokedMethod
import org.testng.ISuite
import org.testng.ITestContext
import org.testng.ITestResult
import org.testng.Reporter
import org.testng.SkipException
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.time.LocalTime
import java.util.*
import javax.imageio.ImageIO

/**
 * Utility class that provides various helper methods that can be invoked
 * from a Velocity template.
 */
class ReportNGUtils {
    /**
     * Retrieves all screenshots associated with a particular test result.
     *
     * @param result Which test result to look-up.
     * @return A list of screenshot absolute file paths.
     */
    @Throws(IOException::class)
    fun getScreenshots(result: ITestResult): List<String> {
        val outputDirectory = result.testContext.outputDirectory
        val className = result.testClass.name
        val testName = result.method.methodName
        val list: MutableList<String> = ArrayList<String>()
        val screenshotPath = pathToScreenshot(outputDirectory, className, testName)
        //println("screenshotPath: $screenshotPath")
        val file = File(screenshotPath)
        if (file.exists() && !file.isDirectory) {
            list.add(ImageToBase64.encodeToString(ImageIO.read(file), "png"))
            //list.add("images" + "/" + className + "/" + testName + ".png");
        }
        return list
    }

    /**
     * Path to screenshot.
     */
    private fun pathToScreenshot(outputDirectory: String, className: String, testName: String): String {
        return "$outputDirectory/images/$className/$testName.png"
    }

    /**
     * Returns the aggregate of the elapsed times for each test result.
     *
     * @param context The test results.
     * @return The sum of the test durations.
     */
    fun getDuration(context: ITestContext): Long {
        var duration = getDuration(context.passedConfigurations.allResults)
        duration += getDuration(context.passedTests.allResults)
        // You would expect skipped tests to have durations of zero, but apparently not.
        //duration += getDuration(context.skippedConfigurations.allResults)
        //duration += getDuration(context.skippedTests.allResults)
        //duration += getDuration(context.failedConfigurations.allResults)
        //duration += getDuration(context.failedTests.allResults)
        return duration
    }

    /**
     * Returns the aggregate of the elapsed times for each test result.
     *
     * @param results A set of test results.
     * @return The sum of the test durations.
     */
    private fun getDuration(results: Set<ITestResult>): Long {
        var duration: Long = 0
        for (result in results) {
            duration += result.endMillis - result.startMillis
        }
        return duration
    }


    /**
     * Return first group to which test belongs.
     */
    fun getIncludedGroups(context: ITestContext): String {
        return context.allTestMethods
            .mapNotNull { it.groups }
            .sortedByDescending { it.count() }
            .mapNotNull { it.firstOrNull()}
            .toSet().distinct().sorted().joinToString()
    }

    /**
     * Get formatted total duration for entire suite
     *
     * @param suite test suite
     * @return formatted total duration for entire suite
     */
    fun totalDuration(suite: ISuite): String {
        var totalDuration: Long = 0
        for (value in suite.results.values) {
            val duration = getDuration(value.testContext)
            if (duration > 0) totalDuration += duration
        }
        val seconds = (totalDuration.toDouble() / 1000)
        return formatDurationInTime(seconds.toLong())
    }

    /**
     * Time duration in (hh:MM:SS) format
     */
    fun formatDurationInTime(seconds: Long): String {
        return if (seconds >= 0) {
            var time = LocalTime.MIN.plusSeconds(seconds).toString()
            if (time.length == 5) time = "00:$time"
            time
        } else {
            "**:**:**"
        }
    }

    /**
     * Time duration in short format
     * 00:00:01 ->        1s
     * 00:00:21 ->       21s
     * 00:03:21 ->     3:21
     * 00:43:21 ->    43:21
     * 05:43:21 ->  5:43:21
     * 65:43:21 -> 65:43:21
     */
    fun formatDurationInTimeShort(seconds: Long): String {
        val long = formatDurationInTime(seconds)
        var short = long.replace("00:00:0", "").replace("00:00:", "").replace("00:0:", "").replace("00:", "")
        if (short.length > 1 && short[0] == '0') {
            short = short.replaceFirst("0", "")
        }
        if (short.length <= 2 && short != "0") short += "s"
        return short
    }

    fun formatDuration(startMillis: Long, endMillis: Long): String {
        val elapsed = endMillis - startMillis
        return formatDuration(elapsed)
    }

    fun formatDuration(elapsed: Long): String {
        val seconds = elapsed.toDouble() / 1000
        return formatDurationInTimeShort(seconds.toLong())
    }

    /**
     * Convert a Throwable into a list containing all of its causes.
     *
     * @param t The throwable for which the causes are to be returned.
     * @return A (possibly empty) list of [Throwable]s.
     */
    fun getCauses(t: Throwable?): List<Throwable?> {
        val causes: MutableList<Throwable?> = LinkedList()
        var next = t
        while (next!!.cause != null) {
            next = next.cause
            causes.add(next)
        }
        return causes
    }

    /**
     * Retrieves all log messages associated with a particular test result.
     *
     * @param result Which test result to look-up.
     * @return A list of log messages.
     */
    fun getTestOutput(result: ITestResult?): List<String> {
        return Reporter.getOutput(result)
    }

    /**
     * Retieves the output from all calls to [org.testng.Reporter.log]
     * across all tests.
     *
     * @return A (possibly empty) list of log messages.
     */
    val allOutput: List<String>
        get() = Reporter.getOutput()

    fun hasArguments(result: ITestResult): Boolean {
        return result.parameters.isNotEmpty()
    }

    fun getArguments(result: ITestResult): String {
        val arguments = result.parameters
        val argumentStrings: MutableList<String> = ArrayList(arguments.size)
        for (argument in arguments) {
            argumentStrings.add(renderArgument(argument))
        }
        return commaSeparate(argumentStrings)
    }

    /**
     * Decorate the string representation of an argument to give some
     * hint as to its type (e.g. render Strings in double quotes).
     *
     * @param argument The argument to render.
     * @return The string representation of the argument.
     */
    private fun renderArgument(argument: Any?): String {
        return when (argument) {
            null -> {
                "null"
            }

            is String -> {
                "\"" + argument + "\""
            }

            is Char -> {
                "\'" + argument + "\'"
            }

            else -> {
                argument.toString()
            }
        }
    }

    /**
     * @param result The test result to be checked for dependent groups.
     * @return True if this test was dependent on any groups, false otherwise.
     */
    fun hasDependentGroups(result: ITestResult): Boolean {
        return result.method.groupsDependedUpon.isNotEmpty()
    }

    /**
     * @return A comma-separated string listing all dependent groups.  Returns an
     * empty string it there are no dependent groups.
     */
    fun getDependentGroups(result: ITestResult): String {
        val groups = result.method.groupsDependedUpon
        return commaSeparate(listOf(*groups))
    }

    /**
     * @param result The test result to be checked for dependent methods.
     * @return True if this test was dependent on any methods, false otherwise.
     */
    fun hasDependentMethods(result: ITestResult): Boolean {
        return result.method.methodsDependedUpon.isNotEmpty()
    }

    /**
     * @return A comma-separated string listing all dependent methods.  Returns an
     * empty string it there are no dependent methods.
     */
    fun getDependentMethods(result: ITestResult): String {
        val methods = result.method.methodsDependedUpon
        return commaSeparate(listOf(*methods))
    }

    fun hasSkipException(result: ITestResult): Boolean {
        return result.throwable is SkipException
    }

    fun getSkipExceptionMessage(result: ITestResult): String? {
        return if (hasSkipException(result)) result.throwable.message else ""
    }

    fun hasGroups(suite: ISuite): Boolean {
        return suite.methodsByGroups.isNotEmpty()
    }

    /**
     * Takes a list of Strings and combines them into a single comma-separated
     * String.
     *
     * @param strings The Strings to combine.
     * @return The combined, comma-separated, String.
     */
    private fun commaSeparate(strings: Collection<String>): String {
        val buffer = StringBuilder()
        val iterator = strings.iterator()
        while (iterator.hasNext()) {
            val string = iterator.next()
            buffer.append(string)
            if (iterator.hasNext()) {
                buffer.append(", ")
            }
        }
        return buffer.toString()
    }

    /**
     * Replace any angle brackets, quotes, apostrophes or ampersands with the
     * corresponding XML/HTML entities to avoid problems displaying the String in
     * an XML document.  Assumes that the String does not already contain any
     * entities (otherwise the ampersands will be escaped again).
     *
     * @param s The String to escape.
     * @return The escaped String.
     */
    fun escapeString(s: String?): String? {
        if (s == null) {
            return null
        }
        val buffer = StringBuilder()
        for (element in s) {
            buffer.append(escapeChar(element))
        }
        return buffer.toString()
    }

    /**
     * Converts a char into a String that can be inserted into an XML document,
     * replacing special characters with XML entities as required.
     *
     * @param character The character to convert.
     * @return An XML entity representing the character (or a String containing
     * just the character if it does not need to be escaped).
     */
    private fun escapeChar(character: Char): String {
        return when (character) {
            '<' -> "&lt;"
            '>' -> "&gt;"
            '"' -> "&quot;"
            '\'' -> "&apos;"
            '&' -> "&amp;"
            else -> character.toString()
        }
    }

    /**
     * Works like [.escapeString] but also replaces line breaks with
     * &lt;br /&gt; tags and preserves significant whitespace.
     *
     * @param s The String to escape.
     * @return The escaped String.
     */
    fun escapeHTMLString(s: String?): String? {
        if (s == null) {
            return null
        }
        val buffer = StringBuilder()
        for (i in s.indices) {
            when (val ch = s[i]) {
                ' ' -> {
                    // All spaces in a block of consecutive spaces are converted to
                    // non-breaking space (&nbsp;) except for the last one.  This allows
                    // significant whitespace to be retained without prohibiting wrapping.
                    val nextCh: Char = if (i + 1 < s.length) s[i + 1] else 0.toChar()
                    buffer.append(if (nextCh == ' ') "&nbsp;" else " ")
                }

                '\n' -> buffer.append("<br/>\n")
                else -> buffer.append(escapeChar(ch))
            }
        }
        return buffer.toString()
    }

    /**
     * TestNG returns a compound thread ID that includes the thread name and its numeric ID,
     * separated by an 'at' sign.  We only want to use the thread name as the ID is mostly
     * unimportant and it takes up too much space in the generated report.
     *
     * @param threadId The compound thread ID.
     * @return The thread name.
     */
    fun stripThreadName(threadId: String?): String? {
        return if (threadId == null) {
            null
        } else {
            val index = threadId.lastIndexOf('@')
            if (index >= 0) threadId.substring(0, index) else threadId
        }
    }

    /**
     * Find the earliest start time of the specified methods.
     *
     * @param methods A list of test methods.
     * @return The earliest start time.
     */
    fun getStartTime(methods: List<IInvokedMethod>): Long {
        var startTime = System.currentTimeMillis()
        for (method in methods) {
            startTime = startTime.coerceAtMost(method.date)
        }
        return startTime
    }

    fun getEndTime(suite: ISuite, method: IInvokedMethod, methods: List<IInvokedMethod>): Long {
        var found = false
        for (m in methods) {
            if (m === method) {
                found = true
            } else if (found && m.testMethod.id == method.testMethod.id) {
                return m.date
            }
        }
        return getEndTime(suite, method)
    }

    /**
     * Returns the timestamp for the time at which the suite finished executing.
     * This is determined by finding the latest end time for each of the individual
     * tests in the suite.
     *
     * @param suite The suite to find the end time of.
     * @return The end time (as a number of milliseconds since 00:00 1st January 1970 UTC).
     */
    private fun getEndTime(suite: ISuite, method: IInvokedMethod): Long {
        // Find the latest end time for all tests in the suite.
        for ((_, value) in suite.results) {
            val testContext = value.testContext
            for (m in testContext.allTestMethods) {
                if (method === m) {
                    return testContext.endDate.time
                }
            }
            // If we can't find a matching test method it must be a configuration method.
            for (m in testContext.passedConfigurations.allMethods) {
                if (method === m) {
                    return testContext.endDate.time
                }
            }
            for (m in testContext.failedConfigurations.allMethods) {
                if (method === m) {
                    return testContext.endDate.time
                }
            }
        }
        throw IllegalStateException("Could not find matching end time.")
    }

    fun formatPercentage(numerator: Int, denominator: Int): String {
        return PERCENTAGE_FORMAT.format(numerator / denominator.toDouble())
    }

    companion object {
        private val PERCENTAGE_FORMAT: NumberFormat = DecimalFormat("#0.00%", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
    }
}