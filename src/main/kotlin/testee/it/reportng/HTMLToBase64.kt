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
        if (html.contains("</body>")) {
            html = html.substring(html.indexOf("<body"), html.indexOf("</body>"))
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
        styleSheet.addRule("h2 {font-family: Arial, Helvetica, sans-serif; font-weight: bold; font-size: 1.2em; margin-bottom: 0.6667em;}")
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
        styleSheet.addRule("only-passed {background-color: #eeeeee;}")
        styleSheet.addRule("only-skipped {background-color: #eeeeee;}")
        styleSheet.addRule("only-failed {background-color: #eeeeee;}")
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
        styleSheet.addRule(".resultsTable .method {width: 40em;}")
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
        styleSheet.addRule(".description {color: black;}")
        styleSheet.addRule(".descriptionOutput {font-family: Lucida Console, Monaco, Courier New, monospace; color: #666666; font-size: 0.9em; line-height: 1.2em; margin-left: 1em; display: none;}")
        styleSheet.addRule(".testOutput {font-family: Lucida Console, Monaco, Courier New, monospace;}")
        styleSheet.addRule(".stackTrace {font-size: 0.7em; line-height: 1.2em; margin-left: 1em; display: none;}")
        styleSheet.addRule(".stackTrace .stackTrace {font-size: inherit;}")
        styleSheet.addRule("#meta {font-size: 1.5em; text-align: right; float: right;}")
        styleSheet.addRule("#systemInfo {color: #666666;}")
        /* Report er log output (individual test ouput is style by "testOutput" above). */
        styleSheet.addRule("#log {font-family: Lucida Console, Monaco, Courier New, monospace; font-size: 1.8em; margin-top: 1.8em;}")
        styleSheet.addRule(".titleTable {width: 100%; margin-top: 1.8em; line-height: 1.7em; border-spacing: 0.1em;}")
        styleSheet.addRule(".titleTable tr {height: 1.6em;}")
        styleSheet.addRule(".overviewTable {margin-top: 10px; width: 100%; line-height: 1.7em; border-spacing: 0.1em;}")
        styleSheet.addRule(".overviewTable tr {height: 1.6em;}")
        styleSheet.addRule(".overviewTable td {padding: 0 1em;}")
        styleSheet.addRule(".overviewTable th {padding: 0 .5em;}")
        styleSheet.addRule(".overviewTable .duration {width: 6em;}")
        styleSheet.addRule(".overviewTable .passRate {width: 6em;}")
        styleSheet.addRule(".overviewTable .number {width: 5em;}")
        styleSheet.addRule(".progressTable {margin-top: 10px;")
        styleSheet.addRule(".progressLine {font-size: 0.8em;}")
        styleSheet.addRule(".reveal a {text-decoration-line:none;}")
        return styleSheet
    }

    var HTML_EXAMPLE = """<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title>e2e</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta name="description" content="TestNG unit test results."/>
    <link href="reportng.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="reportng.js"></script>
</head>
<body style="background-color:#f0f8ff">
<table class="titleTable">
    <tr>
        <td class="h2" style="width:40%">e2e-CRM-develop-6477f447a1fa22d6900a6a7859d15da1893767e1</td>
        <td id="meta">Info: <a href=https://github.com/hibissscus/testee target="_top">Testee</a>generated by &#127752;<a href="https://github.com/hibissscus/reportng" target="_top">ReportNG</a> 1.5.2 at 16:50 CET on Tuesday 13 December 2022<br/><span id="systemInfo">sstepanov@IS-0200&nbsp;/&nbsp;Java 18.0.2.1 (Oracle Corporation)&nbsp;/&nbsp;Mac OS X 13.0.1 (aarch64)</span></td>
    </tr>
</table>
<table class="progressTable" border="0" cellspacing="0" cellpadding="0" style="width:101.30%;">
    <tbody>
    <tr class="progressLine" style="height: 3px">
        <td class="progressLine" style="width:0%;background-color:#999999;"></td>
        <td class="progressLine" style="width:50.00%;background-color:#44aa44;"></td>
        <td class="progressLine" style="width:2.88%;background-color:#ffaa00;"></td>
        <td class="progressLine" style="width:47.12%;background-color:#ff4444;"></td>
    </tr>
    </tbody>
</table>
<table class="overviewTable">
    <tr class="columnHeadings">
        <th><a href="suite1_groups.html">Groups</a>
        </th>
        <th>Groups</th>
        <th>Duration</th>
        <th class="pointer" onclick="toggleElements('only-passed', ''); toggle('toggle-only-passed')"><span id="toggle-only-passed" class="toggle">&#x25bc;</span>Passed
        </th>
        <th class="pointer" onclick="toggleElements('only-skipped', ''); toggle('toggle-only-skipped')"><span id="toggle-only-skipped" class="toggle">&#x25bc;</span>Skipped
        </th>
        <th class="pointer toggle-failed" onclick="toggleElements('only-failed', ''); toggle('toggle-only-failed')"><span id="toggle-only-failed" class="toggle">&#x25bc;</span>Failed
        </th>
        <th>Pass Rate</th>
    </tr>
    <tr class="test only-passed">
        <td class="test"><a href="suite1_test1_results.html">successful tests 100%</a></td>
        <td>successful</td>
        <td class="duration">0</td>
        <td class="passed"><span class="number">4</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="passRate">100.00%</td>
    </tr>
    <tr class="test ">
        <td class="test"><a href="suite1_test2_results.html">partial tests 90%</a></td>
        <td>partial</td>
        <td class="duration">9s</td>
        <td class="passed"><span class="number">9</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="failed"><span class="number">1</span></td>
        <td class="passRate">90.00%</td>
    </tr>
    <tr class="test ">
        <td class="test"><a href="suite1_test3_results.html">partial tests 80%</a></td>
        <td>partial</td>
        <td class="duration">8s</td>
        <td class="passed"><span class="number">8</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="failed"><span class="number">2</span></td>
        <td class="passRate">80.00%</td>
    </tr>
    <tr class="test ">
        <td class="test"><a href="suite1_test4_results.html">partial tests 70%</a></td>
        <td>partial</td>
        <td class="duration">7s</td>
        <td class="passed"><span class="number">7</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="failed"><span class="number">3</span></td>
        <td class="passRate">70.00%</td>
    </tr>
    <tr class="test ">
        <td class="test"><a href="suite1_test5_results.html">partial tests 60%</a></td>
        <td>partial</td>
        <td class="duration">6s</td>
        <td class="passed"><span class="number">6</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="failed"><span class="number">4</span></td>
        <td class="passRate">60.00%</td>
    </tr>
    <tr class="test ">
        <td class="test"><a href="suite1_test6_results.html">suite all tests</a></td>
        <td>dependant</td>
        <td class="duration">46s</td>
        <td class="passed"><span class="number">59</span></td>
        <td class="skipped"><span class="number">5</span></td>
        <td class="failed"><span class="number">51</span></td>
        <td class="passRate">51.30%</td>
    </tr>
    <tr class="test ">
        <td class="test"><a href="suite1_test7_results.html">partial tests 50%</a></td>
        <td>partial</td>
        <td class="duration">5s</td>
        <td class="passed"><span class="number">5</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="failed"><span class="number">5</span></td>
        <td class="passRate">50.00%</td>
    </tr>
    <tr class="test ">
        <td class="test"><a href="suite1_test8_results.html">suite all partial tests</a></td>
        <td>partial</td>
        <td class="duration">46s</td>
        <td class="passed"><span class="number">45</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="failed"><span class="number">45</span></td>
        <td class="passRate">50.00%</td>
    </tr>
    <tr class="test ">
        <td class="test"><a href="suite1_test9_results.html">dependant tests 40%</a></td>
        <td>dependant</td>
        <td class="duration">0</td>
        <td class="passed"><span class="number">2</span></td>
        <td class="skipped"><span class="number">2</span></td>
        <td class="failed"><span class="number">1</span></td>
        <td class="passRate">40.00%</td>
    </tr>
    <tr class="test ">
        <td class="test"><a href="suite1_test10_results.html">partial tests 40%</a></td>
        <td>partial</td>
        <td class="duration">5s</td>
        <td class="passed"><span class="number">4</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="failed"><span class="number">6</span></td>
        <td class="passRate">40.00%</td>
    </tr>
    <tr class="test ">
        <td class="test"><a href="suite1_test11_results.html">allkind tests 33%</a></td>
        <td>allkind</td>
        <td class="duration">0</td>
        <td class="passed"><span class="number">1</span></td>
        <td class="skipped"><span class="number">1</span></td>
        <td class="failed"><span class="number">1</span></td>
        <td class="passRate">33.33%</td>
    </tr>
    <tr class="test ">
        <td class="test"><a href="suite1_test12_results.html">partial tests 30%</a></td>
        <td>partial</td>
        <td class="duration">3s</td>
        <td class="passed"><span class="number">3</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="failed"><span class="number">7</span></td>
        <td class="passRate">30.00%</td>
    </tr>
    <tr class="test ">
        <td class="test"><a href="suite1_test13_results.html">partial tests 20%</a></td>
        <td>partial</td>
        <td class="duration">2s</td>
        <td class="passed"><span class="number">2</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="failed"><span class="number">8</span></td>
        <td class="passRate">20.00%</td>
    </tr>
    <tr class="test ">
        <td class="test"><a href="suite1_test14_results.html">partial tests 10%</a></td>
        <td>partial</td>
        <td class="duration">1s</td>
        <td class="passed"><span class="number">1</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="failed"><span class="number">9</span></td>
        <td class="passRate">10.00%</td>
    </tr>
    <tr class="test only-failed">
        <td class="test"><a href="suite1_test15_results.html">failed tests</a></td>
        <td>failed</td>
        <td class="duration">0</td>
        <td class="zero"><span class="number">0</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="failed"><span class="number">4</span></td>
        <td class="passRate">0.00%</td>
    </tr>
    <tr class="test only-skipped">
        <td class="test"><a href="suite1_test16_results.html">skipped tests</a></td>
        <td>skipped</td>
        <td class="duration">0</td>
        <td class="zero"><span class="number">0</span></td>
        <td class="skipped"><span class="number">1</span></td>
        <td class="zero"><span class="number">0</span></td>
        <td class="passRate">0.00%</td>
    </tr>
    <tr class="suite">
        <td colspan="2" class="totalLabel" style="background-color:#f0f8ff">Total</td>
        <td class="duration"><span class="number">00:02:18</span></td>
        <td class="passed"><span class="number">156</span></td>
        <td class="skipped"><span class="number">9</span></td>
        <td class="failed"><span class="number">147</span></td>
        <td class="passRate"><span class="suite">50.00%</span></td>
    </tr>
</table>
</body>
</html>
"""
}