package testee.it.reportng.example.sample

import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * A configuration failure for testing that the report correctly reports
 * configuration failures.
 */
class FailedConfiguration {
    /**
     * A configuration method that will fail causing any test cases
     * in this class to be skipped.
     */
    @BeforeClass
    fun configure() {
        throw RuntimeException("Configuration failed.")
    }

    /**
     * This test ought to be skipped since the configuration for this
     * class will fail.
     */
    @Test
    fun thisShouldBeSkipped() {
        assert(false) { "This method is supposed to be skipped." }
    }
}