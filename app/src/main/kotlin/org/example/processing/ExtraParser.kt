package org.example.processing

import org.example.model.ExtraSchema
import org.example.model.ProductType

class ExtraParseException(message: String) : Exception(message)

object ExtraParser {
    fun parse(raw: String, type: ProductType): Map<String, String> {
        if (raw.isBlank()) return emptyMap()
        val result = linkedMapOf<String, String>()
        raw.split(';').forEach { pair ->
            val trimmed = pair.trim()
            if (trimmed.isEmpty()) return@forEach
            val separator = trimmed.indexOf('=')
            if (separator <= 0 || separator == trimmed.lastIndex) {
                throw ExtraParseException("Некорректная пара в extra: '$trimmed'")
            }
            val key = trimmed.substring(0, separator).trim()
            val value = trimmed.substring(separator + 1).trim()
            if (ExtraSchema.isAllowed(type, key)) {
                result[key] = value
            }
        }
        return result
    }
}
