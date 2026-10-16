package org.example.processing

data class ParsedWeight(
    val amount: Double,
    val unit: WeightUnit,
)

enum class WeightUnit { KG, LB }

class WeightParseException(message: String) : Exception(message)

object WeightParser {
    const val LB_TO_KG = 0.454

    private val pattern = Regex("""^(-?\d+(?:\.\d+)?)\s*(kg|lb)$""", RegexOption.IGNORE_CASE)

    fun parse(raw: String): ParsedWeight {
        val match = pattern.matchEntire(raw.trim())
            ?: throw WeightParseException("Не удалось распарсить вес '$raw'")
        val amount = match.groupValues[1].toDouble()
        val unit = when (match.groupValues[2].lowercase()) {
            "kg" -> WeightUnit.KG
            "lb" -> WeightUnit.LB
            else -> throw WeightParseException("Не удалось распарсить вес '$raw'")
        }
        return ParsedWeight(amount, unit)
    }

    fun toKilograms(parsed: ParsedWeight): Double = when (parsed.unit) {
        WeightUnit.KG -> parsed.amount
        WeightUnit.LB -> parsed.amount * LB_TO_KG
    }
}
