package testee.it.reportng

import org.testng.annotations.Test

/**
 * Unit test for [ReportNGUtils].
 */
class ReportNGUtilsTest {

    private val utils = ReportNGUtils()

    @Test
    fun testFormatDurationInTime() {
        val formatDurationA = utils.formatDurationInTime(1L)
        assert(formatDurationA == "00:00:01") { "Wrong time duration format: $formatDurationA" }

        val formatDurationB = utils.formatDurationInTime(3600L)
        assert(formatDurationB == "00:01:00") { "Wrong time duration format: $formatDurationB" }

        val formatDurationC = utils.formatDurationInTime(36000L)
        assert(formatDurationC == "00:10:00") { "Wrong time duration format: $formatDurationC" }

        val formatDurationD = utils.formatDurationInTime(86401L)
        assert(formatDurationD == "00:00:01") { "Wrong time duration format: $formatDurationD" }

        val formatDurationE = utils.formatDurationInTime(86399L)
        assert(formatDurationE == "23:59:59") { "Wrong time duration format: $formatDurationE" }
    }

    @Test
    fun testFormatDurationInTimeShort() {
        val formatDurationA = utils.formatDurationInTimeShort(1L)
        assert(formatDurationA == "1s") { "Wrong time duration format: $formatDurationA" }

        val formatDurationB = utils.formatDurationInTimeShort(3600L)
        assert(formatDurationB == "1:00") { "Wrong time duration format: $formatDurationB" }

        val formatDurationC = utils.formatDurationInTimeShort(36000L)
        assert(formatDurationC == "10:00") { "Wrong time duration format: $formatDurationC" }

        val formatDurationD = utils.formatDurationInTimeShort(86401L)
        assert(formatDurationD == "1s") { "Wrong time duration format: $formatDurationD" }

        val formatDurationE = utils.formatDurationInTimeShort(86399L)
        assert(formatDurationE == "23:59:59") { "Wrong time duration format: $formatDurationE" }

        val formatDurationF = utils.formatDurationInTimeShort(59L)
        assert(formatDurationF == "59s") { "Wrong time duration format: $formatDurationF" }
    }

    @Test
    fun testFormatNegativeDuration() {
        val formatDuration = utils.formatDuration(-36000L)
        assert(formatDuration == "**:**:**") { "Wrong duration format: $formatDuration" }
    }

    @Test
    fun testEscapeTags() {
        val originalString = "</ns1:ErrorCode>"
        val escapedString = utils.escapeString(originalString)
        assert(escapedString == "&lt;/ns1:ErrorCode&gt;") { "Wrong escaping: $escapedString" }
    }

    @Test
    fun testEscapeQuotes() {
        val originalString = "\"Hello\'"
        val escapedString = utils.escapeString(originalString)
        assert(escapedString == "&quot;Hello&apos;") { "Wrong escaping: $escapedString" }
    }

    @Test
    fun testEscapeAmpersands() {
        val originalString = "&&"
        val escapedString = utils.escapeString(originalString)
        assert(escapedString == "&amp;&amp;") { "Wrong escaping: $escapedString" }
    }

    @Test
    fun testEscapeSpaces() {
        val originalString = "    "
        // Spaces should not be escaped in XML...
        var escapedString = utils.escapeString(originalString)
        assert(escapedString == originalString) { "Wrong escaping: $escapedString" }
        // ...only in HTML.
        escapedString = utils.escapeHTMLString(originalString)
        assert(escapedString == "&nbsp;&nbsp;&nbsp; ") { "Wrong escaping: $escapedString" }
    }

    @Test
    fun testFormatIntegerPercentage() {
        val percentage = utils.formatPercentage(10, 100)
        assert("10.00%" == percentage) { "Wrongly formatted percentage: $percentage" }
    }

    @Test
    fun testFormatFractionalPercentage() {
        val percentage = utils.formatPercentage(2, 3)
        assert("66.67%" == percentage) { "Wrongly formatted percentage: $percentage" }
    }
}