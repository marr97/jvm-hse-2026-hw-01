package org.example.model

enum class ProductType(val csvValue: String) {
    OIL("oil"),
    TIRE("tire"),
    ACCESSORY("accessory");

    companion object {
        fun fromCsv(value: String): ProductType? =
            entries.find { it.csvValue.equals(value.trim(), ignoreCase = true) }
    }
}
