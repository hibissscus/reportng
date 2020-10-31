package testee.it.reportng

import org.testng.annotations.Test
import java.util.Locale

/**
 * Unit test for the [ReportMetadata] class.
 */
class ReportMetadataTest {

    @Test
    fun testDefaultLocale() {
        // Unset any previously set property.
        System.getProperties().remove(ReportMetadata.LOCALE_KEY)
        // Make sure we know what the default locale is before we start.
        Locale.setDefault(Locale("en", "GB"))
        val metadata = ReportMetadata()
        val locale = metadata.getLocale().toString()
        assert(locale == "en_GB") { "Wrong locale: $locale" }
    }

    @Test
    fun testLocaleLanguageOnly() {
        // Unset any previously set property.
        System.setProperty(ReportMetadata.LOCALE_KEY, "fr")
        val metadata = ReportMetadata()
        val locale = metadata.getLocale().toString()
        assert(locale == "fr") { "Wrong locale: $locale" }
    }

    @Test
    fun testLocaleLanguageAndCountry() {
        // Unset any previously set property.
        System.setProperty(ReportMetadata.LOCALE_KEY, "fr_CA")
        val metadata = ReportMetadata()
        val locale = metadata.getLocale().toString()
        assert(locale == "fr_CA") { "Wrong locale: $locale" }
    }

    @Test
    fun testLocaleLanguageCountryAndVariant() {
        // Unset any previously set property.
        System.setProperty(ReportMetadata.LOCALE_KEY, "fr_CA_POSIX")
        val metadata = ReportMetadata()
        val locale = metadata.getLocale().toString()
        assert(locale == "fr_CA_POSIX") { "Wrong locale: $locale" }
    }
}