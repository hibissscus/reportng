package testee.it.reportng

import java.io.File
import java.net.InetAddress
import java.net.UnknownHostException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Provides access to static information useful when generating a report.
 */
class ReportMetadata {

    companion object {
        private const val PROPERTY_KEY_PREFIX = "testee.it"
        const val TESTEE_VERSION = "$PROPERTY_KEY_PREFIX.version"
        private const val PREFIX_REPORTNG = "$PROPERTY_KEY_PREFIX.reportng"
        const val SLACK = "$PREFIX_REPORTNG.slack"
        const val SLACK_TOKEN = "$SLACK.token"
        const val SLACK_CHANNEL = "$SLACK.channel"
        const val TITLE_KEY = "$PREFIX_REPORTNG.title"
        const val DEFAULT_TITLE = "Test Results Report"
        const val COVERAGE_KEY = "$PREFIX_REPORTNG.coverage-report"
        const val EXCEPTIONS_KEY = "$PREFIX_REPORTNG.show-expected-exceptions"
        const val OUTPUT_KEY = "$PREFIX_REPORTNG.escape-output"
        const val XML_DIALECT_KEY = "$PREFIX_REPORTNG.xml-dialect"
        const val STYLESHEET_KEY = "$PREFIX_REPORTNG.stylesheet"
        const val LOCALE_KEY = "$PREFIX_REPORTNG.locale"
        const val VELOCITY_LOG_KEY = "$PREFIX_REPORTNG.velocity-log"
        private val DATE_FORMAT: DateFormat = SimpleDateFormat("EEEE dd MMMM yyyy")
        private val TIME_FORMAT: DateFormat = SimpleDateFormat("HH:mm z")
    }

    /**
     * @return string representation of the Testee version.
     */
    fun getTesteeVersion(): String? {
        return System.getProperty(ReportMetadata.TESTEE_VERSION)
    }

    /**
     * The date/time at which this report is being generated.
     */
    private val reportTime = Date()

    /**
     * @return is allowed to send Slack notification.
     */
    fun allowSlackNotification(): Boolean {
        return System.getProperty(SLACK, "false").equals("true", ignoreCase = true)
    }

    /**
     * @return string representation of the Slack token.
     */
    fun getSlackToken(): String? {
        return System.getProperty(ReportMetadata.SLACK_TOKEN)
    }

    /**
     * @return string representation of the Slack channel.
     */
    fun getSlackChannel(): String? {
        return System.getProperty(ReportMetadata.SLACK_CHANNEL)
    }

    /**
     * @return string representation of the report date.
     */
    fun getReportDate(): String? {
        return DATE_FORMAT.format(reportTime)
    }


    /**
     * @return string representation of the report time.
     */
    fun getReportTime(): String? {
        return TIME_FORMAT.format(reportTime)
    }

    /**
     * @return string representation of the report title.
     */
    fun getReportTitle(): String? {
        return System.getProperty(TITLE_KEY, ReportMetadata.DEFAULT_TITLE)
    }

    /**
     * @return URL (absolute or relative) of an HTML coverage report associated
     * with the test run.  Null if there is no coverage report.
     */
    fun getCoverageLink(): String? {
        return System.getProperty(ReportMetadata.COVERAGE_KEY)
    }


    /**
     * If a custom CSS file has been specified, returns the path.  Otherwise
     * returns null.
     *
     * @return [File] pointing to the stylesheet, or null if no stylesheet
     * is specified.
     */
    fun getStylesheetPath(): File? {
        return System.getProperty(STYLESHEET_KEY)?.let { File(it) }
    }


    /**
     * Returns false (the default) if stack traces should not be shown for
     * expected exceptions.
     *
     * @return True if stack traces should be shown even for expected exceptions,
     * false otherwise.
     */
    fun shouldShowExpectedExceptions(): Boolean {
        return System.getProperty(EXCEPTIONS_KEY, "false").equals("true", ignoreCase = true)
    }


    /**
     * Returns true (the default) if log text should be escaped when displayed in a
     * report.  Turning off escaping allows you to do something link inserting
     * link tags into HTML reports, but it also means that other output could
     * accidentally corrupt the mark-up.
     *
     * @return True if reporter log output should be escaped when displayed in a
     * report, false otherwise.
     */
    fun shouldEscapeOutput(): Boolean {
        return System.getProperty(OUTPUT_KEY, "true").equals("true", ignoreCase = true)
    }


    /**
     * If the XML dialect has been set to "junit", we will render all skipped tests
     * as failed tests in the XML.  Otherwise we use TestNG's extended version of
     * the XML format that allows for "<skipped>" elements.
     */
    fun allowSkippedTestsInXML(): Boolean {
        return !System.getProperty(XML_DIALECT_KEY, "testng").equals("junit", ignoreCase = true)
    }


    /**
     * @return True if Velocity should generate a log file, false otherwise.
     */
    fun shouldGenerateVelocityLog(): Boolean {
        return System.getProperty(VELOCITY_LOG_KEY, "false").equals("true", ignoreCase = true)
    }


    /**
     * @return The user account used to run the tests and the host name of the
     * test machine.
     * @throws UnknownHostException If there is a problem accessing the machine's host name.
     */
    @Throws(UnknownHostException::class)
    fun getUser(): String? {
        val user = System.getProperty("user.name")
        val host = InetAddress.getLocalHost().hostName
        return "$user@$host"
    }


    fun getJavaInfo(): String? {
        return String.format(
            "Java %s (%s)",
            System.getProperty("java.version"),
            System.getProperty("java.vendor")
        )
    }


    fun getPlatform(): String? {
        return String.format(
            "%s %s (%s)",
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("os.arch")
        )
    }


    /**
     * @return The locale specified by the System properties, or the platform default locale
     * if none is specified.
     */
    fun getLocale(): Locale? {
        if (System.getProperties().containsKey(LOCALE_KEY)) {
            val locale = System.getProperty(LOCALE_KEY)
            val components = locale.split("_".toRegex(), 3).toTypedArray()
            when (components.size) {
                1 -> return Locale(locale)
                2 -> return Locale(components[0], components[1])
                3 -> return Locale(components[0], components[1], components[2])
                else -> System.err.println("Invalid locale specified: $locale")
            }
        }
        return Locale.getDefault()
    }

}