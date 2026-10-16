package org.example.csv

class CsvParseException(message: String) : Exception(message)

class CsvParser {
    fun parseRecords(content: String): List<CsvRecord> {
        val lines = content.split("\r\n", "\n", "\r")
        if (lines.isEmpty() || lines.all { it.isBlank() }) {
            throw CsvParseException("Файл пуст")
        }

        var headerIndex = lines.indexOfFirst { it.isNotBlank() }
        if (headerIndex < 0) {
            throw CsvParseException("Файл пуст")
        }

        val records = mutableListOf<CsvRecord>()
        var dataLineNumber = 0
        for (i in (headerIndex + 1) until lines.size) {
            val raw = lines[i]
            if (raw.isBlank()) continue
            dataLineNumber++
            records.add(
                try {
                    CsvRecord(dataLineNumber, parseLine(raw))
                } catch (ex: CsvParseException) {
                    CsvRecord(dataLineNumber, emptyList(), ex.message)
                },
            )
        }
        return records
    }

    fun parseLine(line: String): List<String> {
        val fields = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val ch = line[i]
            when {
                ch == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                ch == ',' && !inQuotes -> {
                    fields.add(current.toString())
                    current.clear()
                }
                else -> current.append(ch)
            }
            i++
        }
        if (inQuotes) {
            throw CsvParseException("Незакрытые кавычки в строке")
        }
        fields.add(current.toString())
        return fields
    }
}

data class CsvRecord(
    val lineNumber: Int,
    val fields: List<String>,
    val error: String? = null,
)
