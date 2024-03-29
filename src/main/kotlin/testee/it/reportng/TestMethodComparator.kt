package testee.it.reportng

import org.testng.ITestNGMethod

/**
 * Comparator for sorting TestNG test methods.  Sorts method alphabetically
 * (first by fully-qualified class name, then by method name).
 */
internal class TestMethodComparator : Comparator<ITestNGMethod> {

    override fun compare(
        method1: ITestNGMethod,
        method2: ITestNGMethod
    ): Int {
        var compare = method1.realClass.name.compareTo(method2.realClass.name)
        if (compare == 0) {
            compare = method1.methodName.compareTo(method2.methodName)
        }
        return compare
    }
}