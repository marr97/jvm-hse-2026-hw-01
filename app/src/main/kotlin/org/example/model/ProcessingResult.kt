package org.example.model

data class ProcessingResult(
    val processedRows: Int,
    val products: List<Product>,
    val errors: List<ProcessingError>,
) {
    val parseErrorCount: Int get() = errors.count { it.kind == ErrorKind.PARSE }
    val validationErrorCount: Int get() = errors.count { it.kind == ErrorKind.VALIDATION }

    fun statisticsText(): String = buildString {
        appendLine("Обработано строк: $processedRows")
        appendLine("Успешно загружено товаров: ${products.size}")
        appendLine("Ошибок парсинга: $parseErrorCount")
        append("Ошибок валидации: $validationErrorCount")
    }
}
