package testee.it.reportng

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.pathString

/**
 * Utility for file zipping.
 */
object ZipUtils {
    @JvmStatic
    @Throws(IOException::class)
    fun zip(sourceDirPath: String, zipFileName: String): File {
        val tmpZipDir = Files.createTempDirectory(Paths.get(sourceDirPath), "tmpZipDir")
        val zipFile = File(tmpZipDir.pathString + File.separator + "$zipFileName.zip")
        val p = Paths.get(zipFile.toString())

        ZipOutputStream(Files.newOutputStream(p)).use { zs ->
            val pp = Paths.get(sourceDirPath)
            Files.walk(pp)
                .filter { path: Path -> !Files.isDirectory(path) }
                .filter { path: Path -> !path.pathString.contains("tmpZipDir") }
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

        Files.copy(zipFile.toPath(), Paths.get(sourceDirPath, "$zipFileName.zip"))
        Files.walk(tmpZipDir.toAbsolutePath())
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);

        return File(Paths.get(sourceDirPath, "$zipFileName.zip").toString())
    }
}