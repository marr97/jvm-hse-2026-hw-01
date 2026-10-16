package org.example.export

import org.example.model.Accessory
import org.example.model.Oil
import org.example.model.Product
import org.example.model.Tire

class XmlExporter : DataExporter {
    override val formatName: String = "xml"
    override val fileExtension: String = "xml"

    override fun export(products: List<Product>): String = buildString {
        appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        appendLine("<products>")
        products.forEach { product ->
            appendLine("""  <product type="${escape(product.type.csvValue)}">""")
            appendLine("    <id>${product.id}</id>")
            appendLine("    <name>${escape(product.name)}</name>")
            appendLine("    <priceRub>${product.priceRub}</priceRub>")
            appendLine("    <weightKg>${product.weightKg}</weightKg>")
            appendLine("    <vehicle>${escape(product.vehicle.name)}</vehicle>")
            when (product) {
                is Oil -> product.volume?.let { appendLine("    <volume>$it</volume>") }
                is Tire -> {
                    product.diameter?.let { appendLine("    <diameter>$it</diameter>") }
                    product.season?.let { appendLine("    <season>${escape(it)}</season>") }
                }
                is Accessory -> {
                    product.material?.let { appendLine("    <material>${escape(it)}</material>") }
                    product.color?.let { appendLine("    <color>${escape(it)}</color>") }
                }
            }
            appendLine("  </product>")
        }
        append("</products>")
    }

    private fun escape(value: String): String = buildString {
        value.forEach { ch ->
            when (ch) {
                '&' -> append("&amp;")
                '<' -> append("&lt;")
                '>' -> append("&gt;")
                '"' -> append("&quot;")
                '\'' -> append("&apos;")
                else -> append(ch)
            }
        }
    }
}
