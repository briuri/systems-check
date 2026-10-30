package buri.sc.util

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

fun main() {
    val downloadsDir = File("/Users/BU/Downloads")
    val dataDir = File("/workspace/everybody-codes/data")
    val fileNamePattern = """everybody_codes_e(\d{4})_q(\d{2})_p(\d)\.txt""".toRegex()

    downloadsDir.listFiles { file ->
        file.isFile && fileNamePattern.matches(file.name)
    }?.forEach { file ->
        val matchResult = fileNamePattern.matchEntire(file.name)
        if (matchResult != null) {
            val (year, quest, part) = matchResult.destructured
            val targetDirectory = File(dataDir, "zNew")
            targetDirectory.mkdirs()
            val inputPath = File(targetDirectory, "$quest-$part-0.txt")
            Files.copy(file.toPath(), inputPath.toPath(), StandardCopyOption.REPLACE_EXISTING)
            val examplePath = File(targetDirectory, "$quest-$part-1.txt")
            examplePath.createNewFile()
            println("Moving ${file.name} to ${inputPath.path}")
        }
    }
}