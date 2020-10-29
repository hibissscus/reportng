package testee.it.reportng

/**
 * Unchecked exception thrown when an unrecoverable error occurs during report
 * generation.
 */
class ReportNGException(string: String?, throwable: Throwable?) : RuntimeException(string, throwable)