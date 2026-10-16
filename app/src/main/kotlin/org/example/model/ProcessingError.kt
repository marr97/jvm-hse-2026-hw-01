package org.example.model

enum class ErrorKind {
    PARSE,
    VALIDATION,
}

data class ProcessingError(
    val lineNumber: Int,
    val kind: ErrorKind,
    val message: String,
) {
    fun toReportLine(): String = "[ОШИБКА] Строка $lineNumber: $message"
}
