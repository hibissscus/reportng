package testee.it.reportng

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JEditorPane
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.html.StyleSheet

object HTMLToBase64 {

    /**
     * Generate base64 image out of html
     *
     * @param htmlString The html to convert
     * @param width image width
     * @param height image height
     * @param filePath file path
     * @return base64 string representing image
     */
    @JvmStatic
    @Throws(IOException::class)
    fun htmlToBase64(htmlString: String, width: Int, height: Int, filePath: String?): String {
        var html = htmlString
        if (html.contains("<body>")) {
            html = html.substring(html.indexOf("<body>") + 7, html.indexOf("</body>"))
            println(html)
        }

        // create html editor + css
        val jp = JEditorPane("text/html", html)
        jp.isEditable = false
        jp.validate()
        val kit = HTMLEditorKit()
        kit.styleSheet = styleSheet()
        val doc = kit.createDefaultDocument()
        jp.editorKit = kit
        jp.document = doc
        jp.text = html
        jp.setSize(width, height)

        val saveimg = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g2draw = saveimg.createGraphics()
        g2draw.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2draw.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2draw.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
        jp.paint(g2draw)

        // manually for testing
        if (!filePath.isNullOrEmpty()) {
            ImageIO.write(saveimg, "png", File(filePath))
        }
        return ImageToBase64.encodeToString(saveimg, "png")
    }

    private fun styleSheet(): StyleSheet {
        val styleSheet = StyleSheet()
        styleSheet.addRule("* {padding: 0; margin: 0;}")
        styleSheet.addRule("a {color: #006699;}")
        styleSheet.addRule("a:visited {color: #003366;}")
        styleSheet.addRule("body {font-family: Lucida Sans Unicode, Lucida Grande, sans-serif; line-height: 1.8em; font-size: 62.5%; margin: 5px}")
        styleSheet.addRule("h1 {font-family: Arial, Helvetica, sans-serif; font-weight: bold; font-size: 1.8em; margin-bottom: 0.6667em;}")
        styleSheet.addRule("h2 {font-family: Arial, Helvetica, sans-serif; font-weight: bold; font-size: 1.8em; margin-bottom: 0.6667em;}")
        styleSheet.addRule("p {font-size: 1.8em;}")
        styleSheet.addRule("td {font-size: 1.8em;}")
        styleSheet.addRule(".h1 {font-family: Arial, Helvetica, sans-serif; font-weight: bold; font-size: 2.0em; margin-bottom: 0.6667em;}")
        styleSheet.addRule(".h2 {font-family: Arial, Helvetica, sans-serif; font-weight: bold; font-size: 1.8em; margin-bottom: 0.6667em;}")
        styleSheet.addRule(".header {font-size: 1.8em; font-weight: bold; text-align: left;}")
        styleSheet.addRule(".passed {background-color: #44aa44;}")
        styleSheet.addRule(".skipped {background-color: #ffaa00;}")
        styleSheet.addRule(".failed {background-color: #ff4444;}")
        styleSheet.addRule(".failedConfig {background-color: #800000; color: #ffffff}")
        styleSheet.addRule(".skippedConfig {background-color: #cc6600; color: #ffffff}")
        styleSheet.addRule(".totalLabel {font-weight: bold; background-color: #ffffff;}")
        styleSheet.addRule(".suite {background-color: #999999; font-weight: bold;}")
        styleSheet.addRule(".test {background-color: #eeeeee; padding-left: 2em;}")
        styleSheet.addRule(".test .passed {background-color: #88ee88;}")
        styleSheet.addRule(".test .skipped {background-color: #ffff77;}")
        styleSheet.addRule(".test .failed {background-color: #ff8888;}")
        styleSheet.addRule(".group {background-color: #cccccc7a; color: #000000; font-weight: bold;}")
        styleSheet.addRule(".suiteLinks {float: right; font-weight: normal; vertical-align: middle;}")
        styleSheet.addRule(".suiteLinks a {color: #ffffff; margin-left: .5em;}")
        styleSheet.addRule(".suiteLinks th {color: #ffffff; margin-left: .5em;}")
        styleSheet.addRule(".passRate {font-weight: bold; text-align: right;}")
        styleSheet.addRule(".duration {text-align: left;}")
        styleSheet.addRule(".thread {white-space: nowrap;}")
        styleSheet.addRule(".resultsTable {border: 0; width: 100%; margin-top: 1.8em; line-height: 1.7em; border-spacing: 0.1em;}")
        styleSheet.addRule(".resultsTable .method {width: 18em;}")
        styleSheet.addRule(".resultsTable .method.pass {width: 100%;}")
        styleSheet.addRule(".resultsTable .duration {width: 6em;}")
        styleSheet.addRule(".resultsTable td {vertical-align: top; padding: 0 1em;}")
        styleSheet.addRule(".resultsTable th {padding: 0 .5em;}")
        styleSheet.addRule(".number {text-align: right;}")
        styleSheet.addRule(".zero {font-weight: normal;}")
        styleSheet.addRule(".columnHeadings {font-size: 1.2em; text-align: center;}")
        styleSheet.addRule(".columnHeadings th {font-weight: normal; text-align: center;}")
        styleSheet.addRule(".configTable {border: 1px solid #800000; color: #800000; margin-bottom: 1.5em;}")
        styleSheet.addRule("#sidebarHeader {padding: 1.8em 1em; margin: 0 -1em 1.8em -1em;}")
        styleSheet.addRule("#suites {line-height: 1.7em; border-spacing: 0.1em; width: 100%;}")
        styleSheet.addRule(".tests {display: table-row-group;}")
        styleSheet.addRule(".header.suite {cursor: pointer; clear: right; height: 1.214em; margin-top: 1px;}")
        styleSheet.addRule("div.test {margin-top: 0.1em; clear: right; font-size: 1.8em;}")
        /* The +/- toggle used in the navigation frame. */
        styleSheet.addRule(".toggle {font-family: monospace; font-weight: bold; padding-left: 2px; padding-right: 5px; color: #777777;}")
        styleSheet.addRule(".successIndicator {float: right; font-family: monospace; font-weight: bold; padding-right: 2px; color: #44aa44;}")
        styleSheet.addRule(".skipIndicator {float: right; font-family: monospace; font-weight: bold; padding-right: 2px; color: #ffaa00;}")
        styleSheet.addRule(".failureIndicator {float: right; font-family: monospace; font-weight: bold; padding-right: 2px; color: #ff4444;}")
        /* These classes are for information about an individual test result. */
        styleSheet.addRule(".result {font-size: 1.1em; vertical-align: middle;}")
        styleSheet.addRule(".dependency {font-family: Lucida Console, Monaco, Courier New, monospace; font-weight: bold;}")
        styleSheet.addRule(".arguments {font-family: Lucida Console, Monaco, Courier New, monospace; font-weight: bold;}")
        styleSheet.addRule(".testOutput {font-family: Lucida Console, Monaco, Courier New, monospace; color: #666666;}")
        styleSheet.addRule(".stackTrace {font-size: 0.9em; line-height: 1.2em; margin-left: 2em; display: none;}")
        styleSheet.addRule(".stackTrace .stackTrace {font-size: inherit;}")
        styleSheet.addRule(".description {border-bottom: 1px dotted #006699;}")
        styleSheet.addRule("#meta {font-size: 1.5em; text-align: right; float: right;}")
        styleSheet.addRule("#systemInfo {color: #666666;}")
        /* Report er log output (individual test ouput is style by "testOutput" above). */
        styleSheet.addRule("#log {font-family: Lucida Console, Monaco, Courier New, monospace; font-size: 1.8em; margin-top: 1.8em;}")
        styleSheet.addRule(".titleTable {width: 100%; margin-top: 1.8em; line-height: 1.7em; border-spacing: 0.1em;}")
        styleSheet.addRule(".titleTable tr {height: 1.6em;}")
        styleSheet.addRule(".overviewTable {width: 100%; margin-top: 1.8em; line-height: 1.7em; border-spacing: 0.1em;}")
        styleSheet.addRule(".overviewTable tr {height: 1.6em;}")
        styleSheet.addRule(".overviewTable td {padding: 0 1em;}")
        styleSheet.addRule(".overviewTable th {padding: 0 .5em;}")
        styleSheet.addRule(".overviewTable .duration {width: 6em;}")
        styleSheet.addRule(".overviewTable .passRate {width: 6em;}")
        styleSheet.addRule(".overviewTable .number {width: 5em;}")
        styleSheet.addRule(".progressTable {margin-top: 10px;")
        styleSheet.addRule(".progressLine {font-size: 0.8em;}")
        return styleSheet
    }

    var HTML_EXAMPLE = """<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="" lang="">
<head>
    <title>reportNG</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta name="description" content="TestNG unit test results."/>
    <link href="reportng.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<table class="titleTable">
    <tr>
        <td class="h1">reportNG</td>
        <td id="meta">
            Info: <a href=https://testee.it/ target="_top">testee.it</a> generated by <a href=https://testng.org/ target="_top">TestNG</a> with <a href="https://testee.it/reportng/" target="_top">ReportNG</a>
            at 13:17 CET on Thursday 03 December 2020
            <br/><span id="systemInfo">sstepanov@ysuras-MacBook-Pro-2.local&nbsp;/&nbsp;Java 1.8.0_241 (Oracle Corporation)&nbsp;/&nbsp;Mac OS X 10.15.7 (x86_64)</span>
        </td>
    </tr>
</table>
<table class="overviewTable">
    <tr class="columnHeadings">
        <td>&nbsp;</td>
        <th>Duration</th>
        <th>Passed</th>
        <th>Skipped</th>
        <th>Failed</th>
        <th>Pass Rate</th>
    </tr>
    <tr class="test">
        <td class="test">
            <a href="suite1_test1_results.html">successful tests 100%</a>
        </td>
        <td class="duration">
            0.004s
        </td>
        <td class="passed"><span class="number">4</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="passRate">
            100.00%
        </td>
    </tr>
    <tr class="test">
        <td class="test">
            <a href="suite1_test2_results.html">partial tests 50%</a>
        </td>
        <td class="duration">
            0.001s
        </td>
        <td class="passed"><span class="number">2</span></td>
        <td class="skipped"><span class="number">1</span></td>
        <td class="failed"><span class="number">1</span></td>
        <td class="passRate">
            50.00%
        </td>
    </tr>
    <tr class="test">
        <td class="test">
            <a href="suite1_test3_results.html">allkind tests 33%</a>
        </td>
        <td class="duration">
            0.001s
        </td>
        <td class="passed"><span class="number">1</span></td>
        <td class="skipped"><span class="number">1</span></td>
        <td class="failed"><span class="number">1</span></td>
        <td class="passRate">
            33.33%
        </td>
    </tr>
    <tr class="test">
        <td class="test">
            <a href="suite1_test4_results.html">failed tests 0%</a>
        </td>
        <td class="duration">
            0.001s
        </td>
        <td class="zero"><span class="number">0</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="failed"><span class="number">4</span></td>
        <td class="passRate">
            0.00%
        </td>
    </tr>
    <tr class="test">
        <td class="test">
            <a href="suite1_test5_results.html">skipped tests 0%</a>
        </td>
        <td class="duration">
            0.000s
        </td>
        <td class="zero"><span class="number">0</span></td>
        <td class="skipped"><span class="number">1</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="passRate">
            0.00%
        </td>
    </tr>
    <tr class="suite">
        <td colspan="2" class="totalLabel">Total</td>
        <td class="passed"><span class="number">7</span></td>
        <td class="skipped"><span class="number">3</span></td>
        <td class="failed"><span class="number">6</span></td>
        <td class="passRate">
            <span class="suite">
                43.75%
            </span>
        </td>
    </tr>
</table>
</body>
</html>"""
}