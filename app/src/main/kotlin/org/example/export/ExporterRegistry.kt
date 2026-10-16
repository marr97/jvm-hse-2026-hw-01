package org.example.export

class ExporterRegistry {
    private val exporters = linkedMapOf<String, DataExporter>()

    fun register(exporter: DataExporter) {
        exporters[exporter.formatName.lowercase()] = exporter
    }

    fun get(formatName: String): DataExporter? = exporters[formatName.lowercase()]

    fun availableFormats(): List<String> = exporters.keys.toList()

    companion object {
        fun default(): ExporterRegistry = ExporterRegistry().apply {
            register(JsonExporter())
            register(XmlExporter())
            register(TextReportExporter())
        }
    }
}
