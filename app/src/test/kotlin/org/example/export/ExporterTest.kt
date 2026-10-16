package org.example.export

import org.example.model.Accessory
import org.example.model.Oil
import org.example.model.Tire
import org.example.model.VehicleType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExporterTest {
    private val products = listOf(
        Oil(1, "Mobil 1, Synthetic", 1125.0, 4.54, VehicleType.PASSENGER, 4.0),
        Tire(3, "Michelin Primacy", 1350.0, 0.3, VehicleType.PASSENGER, 16.0, "SUMMER"),
        Accessory(5, "Щетки", 100.0, 0.1, VehicleType.PASSENGER, "резина", "черный"),
    )

    @Test
    fun `registry contains default formats and accepts new exporter`() {
        val registry = ExporterRegistry.default()
        assertEquals(listOf("json", "xml", "txt"), registry.availableFormats())
        assertTrue(registry.get("JSON") is JsonExporter)

        registry.register(object : DataExporter {
            override val formatName = "csv"
            override val fileExtension = "csv"
            override fun export(products: List<org.example.model.Product>) = "id\n"
        })
        assertEquals("csv", registry.get("csv")?.formatName)
        assertNull(registry.get("yaml"))
    }

    @Test
    fun `json exporter writes products and escapes quotes`() {
        val json = JsonExporter().export(products)
        assertTrue(json.contains("\"name\": \"Mobil 1, Synthetic\""))
        assertTrue(json.contains("\"priceRub\": 1125.0"))
        assertTrue(json.contains("\"volume\": 4.0"))
        assertTrue(json.contains("\"season\": \"SUMMER\""))
        assertTrue(json.contains("\"color\": \"черный\""))
        val escaped = JsonExporter().export(
            listOf(Oil(1, "Say \"hi\"", 1.0, 1.0, VehicleType.TRUCK, null)),
        )
        assertTrue(escaped.contains("Say \\\"hi\\\""))
    }

    @Test
    fun `xml exporter writes tags and escapes special characters`() {
        val xml = XmlExporter().export(products)
        assertTrue(xml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))
        assertTrue(xml.contains("<product type=\"oil\">"))
        assertTrue(xml.contains("<name>Mobil 1, Synthetic</name>"))
        assertTrue(xml.contains("<diameter>16.0</diameter>"))
        val escaped = XmlExporter().export(
            listOf(Accessory(1, "A & B <C>", 1.0, 1.0, VehicleType.MOTORCYCLE, null, null)),
        )
        assertTrue(escaped.contains("A &amp; B &lt;C&gt;"))
    }

    @Test
    fun `text exporter writes human-readable report`() {
        val text = TextReportExporter().export(products)
        assertTrue(text.contains("Отчет по товарам автомагазина"))
        assertTrue(text.contains("Название: Mobil 1, Synthetic"))
        assertTrue(text.contains("Цена: 1125.0 руб"))
        assertTrue(text.contains("Объем: 4.0 л"))
        assertTrue(text.contains("Сезон: SUMMER"))
        assertTrue(text.contains("Цвет: черный"))
    }
}
