package testee.it.reportng

import org.testng.IClass
import java.util.Comparator

/**
 * Comparator for sorting classes alphabetically by fully-qualified name.
 */
internal class TestClassComparator : Comparator<IClass> {
    override fun compare(class1: IClass, class2: IClass): Int {
        return class1.name.compareTo(class2.name)
    }
}