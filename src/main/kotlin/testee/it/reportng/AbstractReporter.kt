package testee.it.reportng

import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import org.testng.IReporter
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Writer
import java.util.ResourceBundle

/**
 * Convenient base class for the ReportNG reporters.
 * Provides common functionality.
 */
abstract class AbstractReporter protected constructor(private val classpathPrefix: String) : IReporter {

    companion object {
        const val TEMPLATE_EXTENSION = ".vm"
        const val ENCODING = "UTF-8"
        const val META_KEY = "meta"
        const val UTILS_KEY = "utils"
        const val MESSAGES_KEY = "messages"
        val META = ReportMetadata()
        val UTILS = ReportNGUtils()
        val MESSAGES = ResourceBundle.getBundle("testee.it.reportng.messages.reportng", META.getLocale())
    }

    init {
        Velocity.setProperty("resource.loader", "classpath")
        Velocity.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader")
        if (!META.shouldGenerateVelocityLog()) {
            Velocity.setProperty("runtime.log.logsystem.class",
                    "org.apache.velocity.runtime.log.NullLogSystem")
        }
        try {
            Velocity.init()
        } catch (ex: Exception) {
            throw ReportNGException("Failed to initialise Velocity.", ex)
        }
    }

    /**
     * Helper method that creates a Velocity context and initialises it
     * with a reference to the ReportNG utils, report metadata and localised messages.
     *
     * @return An initialised Velocity context.
     */
    protected fun createContext(): VelocityContext {
        val context = VelocityContext()
        context.put(META_KEY, META)
        context.put(UTILS_KEY, UTILS)
        context.put(MESSAGES_KEY, MESSAGES)
        return context
    }

    /**
     * Generate the specified output file by merging the specified
     * Velocity template with the supplied context.
     */
    @Throws(Exception::class)
    protected fun generateFile(file: File?,
                               templateName: String,
                               context: VelocityContext?) {
        val writer: Writer = BufferedWriter(FileWriter(file))
        try {
            Velocity.mergeTemplate(classpathPrefix + templateName,
                    ENCODING,
                    context,
                    writer)
            writer.flush()
        } finally {
            writer.close()
        }
    }

    /**
     * Copy a single named resource from the classpath to the output directory.
     *
     * @param outputDirectory The destination directory for the copied resource.
     * @param resourceName    The filename of the resource.
     * @param targetFileName  The name of the file created in outputDirectory.
     * @throws IOException If the resource cannot be copied.
     */
    @Throws(IOException::class)
    protected fun copyClasspathResource(outputDirectory: File?,
                                        resourceName: String,
                                        targetFileName: String?) {
        val resourcePath = classpathPrefix + resourceName
        val resourceStream = javaClass.classLoader.getResourceAsStream(resourcePath)
        copyStream(outputDirectory, resourceStream, targetFileName)
    }

    /**
     * Copy a single named file to the output directory.
     *
     * @param outputDirectory The destination directory for the copied resource.
     * @param sourceFile      The path of the file to copy.
     * @param targetFileName  The name of the file created in outputDirectory.
     * @throws IOException If the file cannot be copied.
     */
    @Throws(IOException::class)
    protected fun copyFile(outputDirectory: File?,
                           sourceFile: File?,
                           targetFileName: String?) {
        val fileStream: InputStream = FileInputStream(sourceFile)
        fileStream.use { copyStream(outputDirectory, it, targetFileName) }
    }

    /**
     * Helper method to copy the contents of a stream to a file.
     *
     * @param outputDirectory The directory in which the new file is created.
     * @param stream          The stream to copy.
     * @param targetFileName  The file to write the stream contents to.
     * @throws IOException If the stream cannot be copied.
     */
    @Throws(IOException::class)
    protected fun copyStream(outputDirectory: File?,
                             stream: InputStream?,
                             targetFileName: String?) {
        val resourceFile = File(outputDirectory, targetFileName)
        var reader: BufferedReader? = null
        var writer: Writer? = null
        try {
            reader = BufferedReader(InputStreamReader(stream, ENCODING))
            writer = BufferedWriter(OutputStreamWriter(FileOutputStream(resourceFile), ENCODING))
            var line = reader.readLine()
            while (line != null) {
                writer.write(line)
                writer.write('\n'.toInt())
                line = reader.readLine()
            }
            writer.flush()
        } finally {
            reader?.close()
            writer?.close()
        }
    }

    /**
     * Deletes any empty directories under the output directory.  These
     * directories are created by TestNG for its own reports regardless
     * of whether those reports are generated.  If you are using the
     * default TestNG reports as well as ReportNG, these directories will
     * not be empty and will be retained.  Otherwise they will be removed.
     *
     * @param outputDirectory The directory to search for empty directories.
     */
    protected fun removeEmptyDirectories(outputDirectory: File) {
        if (outputDirectory.exists()) {
            for (file in outputDirectory.listFiles(EmptyDirectoryFilter())) {
                file.delete()
            }
        }
    }

    private class EmptyDirectoryFilter : FileFilter {
        override fun accept(file: File): Boolean {
            return file.isDirectory && file.listFiles().isEmpty()
        }
    }

}