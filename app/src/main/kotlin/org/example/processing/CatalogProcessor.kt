package org.example.processing

import org.example.csv.CsvParseException
import org.example.csv.CsvParser
import org.example.model.ErrorKind
import org.example.model.ProcessingError
import org.example.model.ProcessingResult
import org.example.model.Product

class CatalogProcessor(
    private val csvParser: CsvParser = CsvParser(),
    private val mapper: ProductMapper = ProductMapper(),
) {
    fun process(content: String): ProcessingResult {
        val records = try {
            csvParser.parseRecords(content)
        } catch (ex: CsvParseException) {
            return ProcessingResult(
                processedRows = 0,
                products = emptyList(),
                errors = listOf(ProcessingError(0, ErrorKind.PARSE, ex.message ?: "Ошибка чтения CSV")),
            )
        }

        val products = mutableListOf<Product>()
        val errors = mutableListOf<ProcessingError>()

        for (record in records) {
            if (record.error != null) {
                errors += ProcessingError(record.lineNumber, ErrorKind.PARSE, record.error)
                continue
            }
            try {
                products += mapper.map(record.fields)
            } catch (ex: RowProcessingException) {
                errors += ProcessingError(record.lineNumber, ex.kind, ex.message)
            }
        }

        return ProcessingResult(
            processedRows = records.size,
            products = products,
            errors = errors,
        )
    }
}
