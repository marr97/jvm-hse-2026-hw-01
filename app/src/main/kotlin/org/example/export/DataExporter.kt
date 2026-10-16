package org.example.export

import org.example.model.Product

interface DataExporter {
    val formatName: String
    val fileExtension: String
    fun export(products: List<Product>): String
}
