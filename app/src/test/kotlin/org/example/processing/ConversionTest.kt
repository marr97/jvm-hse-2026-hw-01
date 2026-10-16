package org.example.processing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConversionTest {
    @Test
    fun `converts dollar prefix to rubles`() {
        val parsed = PriceParser.parse("$12.5")
        assertEquals(Currency.USD, parsed.currency)
        assertEquals(1125.0, PriceParser.toRubles(parsed))
    }

    @Test
    fun `converts dollar suffix to rubles`() {
        val parsed = PriceParser.parse("15$")
        assertEquals(1350.0, PriceParser.toRubles(parsed))
    }

    @Test
    fun `keeps ruble price as is`() {
        val parsed = PriceParser.parse("300.5руб")
        assertEquals(Currency.RUB, parsed.currency)
        assertEquals(300.5, PriceParser.toRubles(parsed))
    }

    @Test
    fun `parses negative price for later validation`() {
        val parsed = PriceParser.parse("-5$")
        assertEquals(-5.0, parsed.amount)
        assertEquals(-450.0, PriceParser.toRubles(parsed))
    }

    @Test
    fun `rejects unparseable price`() {
        val ex = assertFailsWith<PriceParseException> { PriceParser.parse("abc") }
        assertEquals("Не удалось распарсить цену 'abc'", ex.message)
    }

    @Test
    fun `converts pounds to kilograms`() {
        val parsed = WeightParser.parse("10lb")
        assertEquals(4.54, WeightParser.toKilograms(parsed))
    }

    @Test
    fun `keeps kilograms as is`() {
        val parsed = WeightParser.parse("1.2kg")
        assertEquals(1.2, WeightParser.toKilograms(parsed))
    }

    @Test
    fun `rejects unparseable weight`() {
        assertFailsWith<WeightParseException> { WeightParser.parse("10tons") }
    }
}
