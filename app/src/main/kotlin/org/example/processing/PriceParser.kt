package org.example.processing

data class ParsedPrice(
    val amount: Double,
    val currency: Currency,
)

enum class Currency { USD, RUB }

class PriceParseException(message: String) : Exception(message)

object PriceParser {
    const val USD_TO_RUB = 90.0

    private val usdPrefix = Regex("""^\$\s*(-?\d+(?:\.\d+)?)$""")
    private val usdSuffix = Regex("""^(-?\d+(?:\.\d+)?)\s*\$$""")
    private val rubSuffix = Regex("""^(-?\d+(?:\.\d+)?)\s*руб$""")

    fun parse(raw: String): ParsedPrice {
        val value = raw.trim()
        usdPrefix.matchEntire(value)?.let { match ->
            return ParsedPrice(match.groupValues[1].toDouble(), Currency.USD)
        }
        usdSuffix.matchEntire(value)?.let { match ->
            return ParsedPrice(match.groupValues[1].toDouble(), Currency.USD)
        }
        rubSuffix.matchEntire(value)?.let { match ->
            return ParsedPrice(match.groupValues[1].toDouble(), Currency.RUB)
        }
        throw PriceParseException("Не удалось распарсить цену '$raw'")
    }

    fun toRubles(parsed: ParsedPrice): Double = when (parsed.currency) {
        Currency.USD -> parsed.amount * USD_TO_RUB
        Currency.RUB -> parsed.amount
    }
}
