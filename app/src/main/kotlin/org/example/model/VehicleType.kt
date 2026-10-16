package org.example.model

enum class VehicleType {
    PASSENGER,
    TRUCK,
    MOTORCYCLE;

    companion object {
        fun fromCsv(value: String): VehicleType? =
            entries.find { it.name.equals(value.trim(), ignoreCase = true) }
    }
}
