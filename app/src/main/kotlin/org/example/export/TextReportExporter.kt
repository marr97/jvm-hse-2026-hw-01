package org.example.export

import org.example.model.Accessory
import org.example.model.Oil
import org.example.model.Product
import org.example.model.Tire

class TextReportExporter : DataExporter {
    override val formatName: String = "txt"
    override val fileExtension: String = "txt"

    override fun export(products: List<Product>): String = buildString {
        appendLine("Отчет по товарам автомагазина")
        appendLine("==============================")
        if (products.isEmpty()) {
            append("Товары отсутствуют.")
            return@buildString
        }
        products.forEachIndexed { index, product ->
            appendLine()
            appendLine("Товар #${index + 1}")
            appendLine("  ID: ${product.id}")
            appendLine("  Название: ${product.name}")
            appendLine("  Тип: ${product.type.csvValue}")
            appendLine("  Цена: ${product.priceRub} руб")
            appendLine("  Вес: ${product.weightKg} кг")
            appendLine("  Автомобиль: ${product.vehicle.name}")
            when (product) {
                is Oil -> product.volume?.let { appendLine("  Объем: $it л") }
                is Tire -> {
                    product.diameter?.let { appendLine("  Диаметр: $it дюйм") }
                    product.season?.let { appendLine("  Сезон: $it") }
                }
                is Accessory -> {
                    product.material?.let { appendLine("  Материал: $it") }
                    product.color?.let { appendLine("  Цвет: $it") }
                }
            }
        }
    }
}
