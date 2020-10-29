package testee.it.reportng.example.sample

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.util.Arrays

/**
 * An example of using a TestNG DataProviderTest.  This used in the sample
 * report to verify that ReportNG can deal with this scenario correctly.
 */
class DataProviderTest {

    @DataProvider(name = "arrayProvider")
    fun dataArray(): Array<Array<Any>> {
        return arrayOf(arrayOf("One", 1.0), arrayOf("Two", 2.0), arrayOf("Three", 3.0))
    }

    @DataProvider(name = "iteratorProvider")
    fun dataIterator(): Iterator<Array<Any>> {
        return Arrays.asList(arrayOf<Any>("One", 1.0), arrayOf<Any>("Two", 2.0), arrayOf<Any>("Three", 3.0)).iterator()
    }

    @Test(groups = ["should-pass"], dataProvider = "arrayProvider")
    fun testProvider(data1: String?, data2: Double) {
        // Do nothing.
    }

    @Test(groups = ["should-pass"], dataProvider = "iteratorProvider")
    fun testProvider2(data1: String?, data2: Double) {
        // Do nothing.
    }
}