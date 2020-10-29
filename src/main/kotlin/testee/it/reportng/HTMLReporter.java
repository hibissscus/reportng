//=============================================================================
// Copyright 2006-2013 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================

package testee.it.reportng;

import org.apache.velocity.VelocityContext;
import org.testng.IClass;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Enhanced HTML reporter for TestNG that uses Velocity templates to generate its
 * output.
 *
 * @author Daniel Dyer
 * @author Sergei Stepanov
 */
public class HTMLReporter extends AbstractReporter {
    private static final String FRAMES_PROPERTY = "testee.it.reportng.frames";
    private static final String ONLY_FAILURES_PROPERTY = "testee.it.reportng.failures-only";

    private static final String TEMPLATES_PATH = "testee/it/reportng/templates/html/";
    private static final String INDEX_FILE = "index.html";
    private static final String SUITES_FILE = "suites.html";
    private static final String OVERVIEW_FILE = "overview.html";
    private static final String GROUPS_FILE = "groups.html";
    private static final String RESULTS_FILE = "results.html";
    private static final String OUTPUT_FILE = "output.html";
    private static final String CUSTOM_STYLE_FILE = "custom.css";
    private static final String RESULT_IMAGE_FILE = "e2e.png";
    private static final String RESULT_ZIP_FILE = "e2e.zip";

    private static final String SUITE_KEY = "suite";
    private static final String SUITES_KEY = "suites";
    private static final String GROUPS_KEY = "groups";
    private static final String RESULT_KEY = "result";
    private static final String FAILED_CONFIG_KEY = "failedConfigurations";
    private static final String SKIPPED_CONFIG_KEY = "skippedConfigurations";
    private static final String FAILED_TESTS_KEY = "failedTests";
    private static final String SKIPPED_TESTS_KEY = "skippedTests";
    private static final String PASSED_TESTS_KEY = "passedTests";
    private static final String ONLY_FAILURES_KEY = "onlyReportFailures";

    private static final String REPORT_DIRECTORY = "e2e";
    private static final String REPORT_DIRECTORY_IMAGES = "images";

    private static final Comparator<ITestNGMethod> METHOD_COMPARATOR = new TestMethodComparator();
    private static final Comparator<ITestResult> RESULT_COMPARATOR = new TestResultComparator();
    private static final Comparator<IClass> CLASS_COMPARATOR = new TestClassComparator();

    public HTMLReporter() {
        super(TEMPLATES_PATH);
    }

    private static Map<String, ISuiteResult> sortByValue(Map<String, ISuiteResult> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, ISuiteResult>> list =
                new LinkedList<Map.Entry<String, ISuiteResult>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        list.sort(new Comparator<Map.Entry<String, ISuiteResult>>() {
            public int compare(Map.Entry<String, ISuiteResult> o1,
                    Map.Entry<String, ISuiteResult> o2) {

                final double rate1 = rate(o1.getValue());
                final double rate2 = rate(o2.getValue());
                if (rate1 == rate2) {
                    return (o1.getValue()).getTestContext().getName().compareTo((o2.getValue()).getTestContext().getName());
                }
                return rate1 > rate2 ? -1 : 1;
            }

            private double rate(final ISuiteResult result) {
                if (result.getTestContext().getPassedTests().size() <= 0) {
                    return 0;
                } else {
                    final double passedTests = result.getTestContext().getPassedTests().size();
                    final double failedTests = result.getTestContext().getFailedTests().size();
                    final double skippedTests = result.getTestContext().getSkippedTests().size();
                    return passedTests / (passedTests + failedTests + skippedTests) * 100;
                }
            }

        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, ISuiteResult> sortedMap = new LinkedHashMap<String, ISuiteResult>();
        for (Map.Entry<String, ISuiteResult> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    /**
     * Generates a set of HTML files that contain data about the outcome of
     * the specified test suites.
     *
     * @param suites              Data about the test runs.
     * @param outputDirectoryName The directory in which to create the report.
     */
    public void generateReport(List<XmlSuite> xmlSuites, // not used
            List<ISuite> suites,
            String outputDirectoryName) {
        removeEmptyDirectories(new File(outputDirectoryName));

        boolean useFrames = System.getProperty(FRAMES_PROPERTY, "true").equals("true");
        boolean onlyFailures = System.getProperty(ONLY_FAILURES_PROPERTY, "false").equals("true");

        File outputDirectory = new File(outputDirectoryName, REPORT_DIRECTORY);
        outputDirectory.mkdirs();

        //  Sorting TestNG test results by passRate from 100% to 0% and alphabetically by method name.
        for (final ISuite suite : suites) {
            final Map<String, ISuiteResult> results = sortByValue(suite.getResults());
            suite.getResults().clear();
            suite.getResults().putAll(results);
        }

        try {
            if (useFrames) {
                createFrameset(outputDirectory);
            }
            createOverview(suites, outputDirectory, !useFrames, onlyFailures);
            createSuiteList(suites, outputDirectory, onlyFailures);
            createGroups(suites, outputDirectory);
            createResults(suites, outputDirectory, onlyFailures);
            createLog(outputDirectory, onlyFailures);
            copyResources(outputDirectory);
            createBase64Overview(outputDirectory);
            createSlackNotification(outputDirectory);
        } catch (Exception ex) {
            throw new ReportNGException("Failed generating HTML report.", ex);
        }
    }

    /**
     * Create base64 representation of overview.html.
     *
     * @param outputDirectory where overview.html e2e.png is stored.
     */
    private void createBase64Overview(final File outputDirectory) {
        try {
            final String resultFilePath = new File(outputDirectory, RESULT_IMAGE_FILE).toPath().toString();
            // create base64 representation of overview.html
            HTMLToBase64.htmlToBase64(
                    new String(Files.readAllBytes(new File(outputDirectory, OVERVIEW_FILE).toPath())),
                    1024, 768,
                    resultFilePath);
        } catch (IOException e) {
            throw new ReportNGException("Failed to create base64 representation of overview.html", e);
        }
    }

    /**
     * Send base64 representation of overview.html to slack channel if it's enabled.
     *
     * @param outputDirectory where overview.html e2e.png is stored.
     */
    private void createSlackNotification(final File outputDirectory) {
        try {
            if (AbstractReporter.META.allowSlackNotification()) {
                final File imageFile = new File(outputDirectory, RESULT_IMAGE_FILE);
                // delete all images before zipping
                final Path imagesPath = Paths.get(outputDirectory.getPath(), REPORT_DIRECTORY_IMAGES);
                if (Files.exists(imagesPath)) {
                    Files.walk(imagesPath)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
                final File zipFile = ZipUtils.zip(outputDirectory.getPath(), "e2e");

                final SlackClient.Slack slack = SlackClient.initialize();
                SlackClient.sendTestReportImageToSlack(
                        slack,
                        AbstractReporter.META.getSlackToken(),
                        AbstractReporter.META.getSlackChanel(),
                        imageFile);
                Thread.sleep(5000L);
                SlackClient.sendTestReportZipToSlack(
                        slack,
                        AbstractReporter.META.getSlackToken(),
                        AbstractReporter.META.getSlackChanel(),
                        zipFile);
            }
        } catch (Exception e) {
            throw new ReportNGException("Failed to send slack test result notification.", e);
        }
    }

    /**
     * Create the index file that sets up the frameset.
     *
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createFrameset(File outputDirectory) throws Exception {
        VelocityContext context = createContext();
        generateFile(new File(outputDirectory, INDEX_FILE),
                INDEX_FILE + TEMPLATE_EXTENSION,
                context);
    }


    private void createOverview(List<ISuite> suites,
            File outputDirectory,
            boolean isIndex,
            boolean onlyFailures) throws Exception {
        VelocityContext context = createContext();
        context.put(SUITES_KEY, suites);
        context.put(ONLY_FAILURES_KEY, onlyFailures);
        generateFile(new File(outputDirectory, isIndex ? INDEX_FILE : OVERVIEW_FILE),
                OVERVIEW_FILE + TEMPLATE_EXTENSION,
                context);
    }


    /**
     * Create the navigation frame.
     *
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createSuiteList(List<ISuite> suites,
            File outputDirectory,
            boolean onlyFailures) throws Exception {
        VelocityContext context = createContext();
        context.put(SUITES_KEY, suites);
        context.put(ONLY_FAILURES_KEY, onlyFailures);
        generateFile(new File(outputDirectory, SUITES_FILE),
                SUITES_FILE + TEMPLATE_EXTENSION,
                context);
    }


    /**
     * Generate a results file for each test in each suite.
     *
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createResults(List<ISuite> suites,
            File outputDirectory,
            boolean onlyShowFailures) throws Exception {
        int index = 1;
        for (ISuite suite : suites) {
            int index2 = 1;
            for (ISuiteResult result : suite.getResults().values()) {
                boolean failuresExist = result.getTestContext().getFailedTests().size() > 0
                        || result.getTestContext().getFailedConfigurations().size() > 0;
                if (!onlyShowFailures || failuresExist) {
                    VelocityContext context = createContext();
                    context.put(RESULT_KEY, result);
                    context.put(FAILED_CONFIG_KEY, sortByTestClass(result.getTestContext().getFailedConfigurations()));
                    context.put(SKIPPED_CONFIG_KEY, sortByTestClass(result.getTestContext().getSkippedConfigurations()));
                    context.put(FAILED_TESTS_KEY, sortByTestClass(result.getTestContext().getFailedTests()));
                    context.put(SKIPPED_TESTS_KEY, sortByTestClass(result.getTestContext().getSkippedTests()));
                    context.put(PASSED_TESTS_KEY, sortByTestClass(result.getTestContext().getPassedTests()));

                    // BYPASS USING THE CONTEXT
                    // the generateFile method uses  Velocity.mergeTemplate( context is passed in here
                    // Note context.put here is passing in a key and IResultMap (
                    // For this to work with screenshots you need to modify the testng framework ( TestRunner class ? )
                    // to have a similar method for getScreenShots that returns an IResultMap and also a method to add a screenshot
                    // conclusion. too complex / too much code to modify ( i.e. not only this class but also the testng framework ).
                    // see http://testng.org/doc/javadocs/org/testng/TestRunner.html

                    String fileName = String.format("suite%d_test%d_%s", index, index2, RESULTS_FILE);
                    generateFile(new File(outputDirectory, fileName),
                            RESULTS_FILE + TEMPLATE_EXTENSION,
                            context);
                }
                ++index2;
            }
            ++index;
        }
    }


    /**
     * Group test methods by class and sort alphabetically.
     */
    private SortedMap<IClass, List<ITestResult>> sortByTestClass(IResultMap results) {
        SortedMap<IClass, List<ITestResult>> sortedResults = new TreeMap<>(CLASS_COMPARATOR);
        for (ITestResult result : results.getAllResults()) {
            List<ITestResult> resultsForClass = sortedResults.computeIfAbsent(result.getTestClass(), k -> new ArrayList<ITestResult>());
            int index = Collections.binarySearch(resultsForClass, result, RESULT_COMPARATOR);
            if (index < 0) {
                index = Math.abs(index + 1);
            }
            resultsForClass.add(index, result);
        }
        return sortedResults;
    }


    /**
     * Generate a groups list for each suite.
     *
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createGroups(List<ISuite> suites,
            File outputDirectory) throws Exception {
        int index = 1;
        for (ISuite suite : suites) {
            SortedMap<String, SortedSet<ITestNGMethod>> groups = sortGroups(suite.getMethodsByGroups());
            if (!groups.isEmpty()) {
                VelocityContext context = createContext();
                context.put(SUITE_KEY, suite);
                context.put(GROUPS_KEY, groups);
                String fileName = String.format("suite%d_%s", index, GROUPS_FILE);
                generateFile(new File(outputDirectory, fileName),
                        GROUPS_FILE + TEMPLATE_EXTENSION,
                        context);
            }
            ++index;
        }
    }


    /**
     * Generate a groups list for each suite.
     *
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createLog(File outputDirectory, boolean onlyFailures) throws Exception {
        if (!Reporter.getOutput().isEmpty()) {
            VelocityContext context = createContext();
            context.put(ONLY_FAILURES_KEY, onlyFailures);
            generateFile(new File(outputDirectory, OUTPUT_FILE),
                    OUTPUT_FILE + TEMPLATE_EXTENSION,
                    context);
        }
    }


    /**
     * Sorts groups alphabetically and also sorts methods within groups alphabetically
     * (class name first, then method name).  Also eliminates duplicate entries.
     */
    private SortedMap<String, SortedSet<ITestNGMethod>> sortGroups(Map<String, Collection<ITestNGMethod>> groups) {
        SortedMap<String, SortedSet<ITestNGMethod>> sortedGroups = new TreeMap<String, SortedSet<ITestNGMethod>>();
        for (Map.Entry<String, Collection<ITestNGMethod>> entry : groups.entrySet()) {
            SortedSet<ITestNGMethod> methods = new TreeSet<ITestNGMethod>(METHOD_COMPARATOR);
            methods.addAll(entry.getValue());
            sortedGroups.put(entry.getKey(), methods);
        }
        return sortedGroups;
    }


    /**
     * Reads the CSS and JavaScript files from the JAR file and writes them to
     * the output directory.
     *
     * @param outputDirectory Where to put the resources.
     * @throws IOException If the resources can't be read or written.
     */
    private void copyResources(File outputDirectory) throws IOException {
        copyClasspathResource(outputDirectory, "reportng.css", "reportng.css");
        copyClasspathResource(outputDirectory, "reportng.js", "reportng.js");
        // If there is a custom stylesheet, copy that.
        File customStylesheet = META.getStylesheetPath();

        if (customStylesheet != null) {
            if (customStylesheet.exists()) {
                copyFile(outputDirectory, customStylesheet, CUSTOM_STYLE_FILE);
            } else {
                // If not found, try to read the file as a resource on the classpath
                // useful when reportng is called by a jarred up library
                InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(customStylesheet.getPath());
                if (stream != null) {
                    copyStream(outputDirectory, stream, CUSTOM_STYLE_FILE);
                }
            }
        }
    }
}
