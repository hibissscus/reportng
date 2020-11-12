package testee.it.reportng

import org.testng.annotations.Test

/**
 * Unit test for [ReportNGUtils].
 */
class ReportNGUtilsTest {

    private val utils = ReportNGUtils()

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