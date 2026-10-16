package org.example.model

object ExtraSchema {
    const val VOLUME = "volume"
    const val DIAMETER = "diameter"
    const val SEASON = "season"
    const val MATERIAL = "material"
    const val COLOR = "color"

    val allowedColors: Set<String> = setOf("черный", "белый", "серебристый")

    val allowedSeasons: Set<String> = setOf("SUMMER", "WINTER", "ALL_SEASON")

    val allowedKeys: Map<ProductType, Set<String>> = mapOf(
        ProductType.OIL to setOf(VOLUME),
        ProductType.TIRE to setOf(DIAMETER, SEASON),
        ProductType.ACCESSORY to setOf(MATERIAL, COLOR),
    )

    fun isAllowed(type: ProductType, key: String): Boolean =
        allowedKeys[type]?.contains(key) == true
}
