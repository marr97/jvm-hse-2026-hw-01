package org.example

import org.example.export.ExporterRegistry
import org.example.processing.CatalogProcessor
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val inputPath = args.getOrNull(0) ?: "input.csv"
    val content = readInput(inputPath) ?: run {
        System.err.println("Файл не найден: $inputPath")
        System.err.println("Укажите путь: <input.csv> [json|xml|txt] [output]")
        exitProcess(1)
    }

    val registry = ExporterRegistry.default()
    val format = args.getOrNull(1) ?: promptFormat(registry)
    val exporter = registry.get(format)
    if (exporter == null) {
        System.err.println("Неизвестный формат '$format'. Доступно: ${registry.availableFormats().joinToString()}")
        exitProcess(1)
    }

    val outputPath = args.getOrNull(2) ?: "output.${exporter.fileExtension}"
    val result = CatalogProcessor().process(content)
    println(result.statisticsText())
    if (result.errors.isNotEmpty()) {
        println()
        result.errors.forEach { println(it.toReportLine()) }
    }

    File(outputPath).writeText(exporter.export(result.products), Charsets.UTF_8)
    println()
    println("Результат сохранен в $outputPath")
}

fun readInput(path: String): String? {
    val candidates = listOf(
        File(path),
        File("app", path),
        File("src/main/resources", File(path).name),
        File("app/src/main/resources", File(path).name),
    )
    candidates.firstOrNull { it.isFile }?.let { return it.readText(Charsets.UTF_8) }

    val resourceName = File(path).name
    val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(resourceName)
        ?: object {}.javaClass.getResourceAsStream("/$resourceName")
    return stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
}

private fun promptFormat(registry: ExporterRegistry): String {
    println("Доступные форматы: ${registry.availableFormats().joinToString()}")
    print("Выберите формат сохранения: ")
    return readlnOrNull()?.trim().orEmpty().ifEmpty { "json" }
}
