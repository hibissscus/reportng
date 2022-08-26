package testee.it.reportng

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Utility for file zipping.
 */
object ZipUtils {
    @JvmStatic
    @Throws(IOException::class)
    fun zip(sourceDirPath: String, zipFileName: String): File {
        val tempZipFile = File.createTempFile(zipFileName, ".zip")
        tempZipFile.deleteOnExit()
        val p = Paths.get(tempZipFile.toString())

        ZipOutputStream(Files.newOutputStream(p)).use { zs ->
            val pp = Paths.get(sourceDirPath)
            Files.walk(pp)
                .filter { path: Path -> !Files.isDirectory(path) }
                .forEach { path: Path ->
                    val zipEntry = ZipEntry(pp.relativize(path).toString())
                    try {
                        zs.putNextEntry(zipEntry)
                        Files.copy(path, zs)
                        zs.closeEntry()
                    } catch (e: IOException) {
                        System.err.println(e)
                    }
                }
        }
        return tempZipFile
    }
}