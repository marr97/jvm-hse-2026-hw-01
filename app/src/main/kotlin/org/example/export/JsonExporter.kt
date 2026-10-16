package org.example.export

import org.example.model.Accessory
import org.example.model.Oil
import org.example.model.Product
import org.example.model.Tire

class JsonExporter : DataExporter {
    override val formatName: String = "json"
    override val fileExtension: String = "json"

    override fun export(products: List<Product>): String = buildString {
        appendLine("[")
        products.forEachIndexed { index, product ->
            append(toJsonObject(product).prependIndent("  "))
            if (index != products.lastIndex) append(",")
            appendLine()
        }
        append("]")
    }

    private fun toJsonObject(product: Product): String {
        val fields = mutableListOf(
            "\"id\": ${product.id}",
            "\"name\": \"${escape(product.name)}\"",
            "\"type\": \"${product.type.csvValue}\"",
            "\"priceRub\": ${product.priceRub}",
            "\"weightKg\": ${product.weightKg}",
            "\"vehicle\": \"${product.vehicle.name}\"",
        )
        when (product) {
            is Oil -> product.volume?.let { fields += "\"volume\": $it" }
            is Tire -> {
                product.diameter?.let { fields += "\"diameter\": $it" }
                product.season?.let { fields += "\"season\": \"${escape(it)}\"" }
            }
            is Accessory -> {
                product.material?.let { fields += "\"material\": \"${escape(it)}\"" }
                product.color?.let { fields += "\"color\": \"${escape(it)}\"" }
            }
        }
        return fields.joinToString(prefix = "{\n  ", separator = ",\n  ", postfix = "\n}")
    }

    private fun escape(value: String): String = buildString {
        value.forEach { ch ->
            when (ch) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(ch)
            }
        }
    }
}
