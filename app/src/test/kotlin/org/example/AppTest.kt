package org.example

import org.example.processing.CatalogProcessor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppTest {
    @Test
    fun `statistics text matches assignment format`() {
        val csv = javaClass.classLoader.getResource("input.csv")!!.readText(Charsets.UTF_8)
        val result = CatalogProcessor().process(csv)
        val stats = result.statisticsText()
        assertTrue(stats.contains("Обработано строк: 12"))
        assertTrue(stats.contains("Успешно загружено товаров: 6"))
        assertTrue(stats.contains("Ошибок парсинга: 3"))
        assertTrue(stats.contains("Ошибок валидации: 3"))
        assertEquals(
            "[ОШИБКА] Строка 7: Не удалось распарсить цену 'abc'",
            result.errors.first().toReportLine(),
        )
    }
}
