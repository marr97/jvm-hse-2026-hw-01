package org.example.processing

import org.example.model.Accessory
import org.example.model.ErrorKind
import org.example.model.ExtraSchema
import org.example.model.Oil
import org.example.model.Product
import org.example.model.ProductType
import org.example.model.Tire
import org.example.model.VehicleType

class ProductMapper {
    fun map(fields: List<String>): Product {
        if (fields.size < 6 || fields.size > 7) {
            throw RowProcessingException(
                ErrorKind.PARSE,
                "Ожидалось 6 или 7 колонок, получено ${fields.size}",
            )
        }

        val idRaw = fields[0].trim()
        val name = fields[1].trim()
        val typeRaw = fields[2].trim()
        val priceRaw = fields[3].trim()
        val weightRaw = fields[4].trim()
        val vehicleRaw = fields[5].trim()
        val extraRaw = fields.getOrElse(6) { "" }

        val id = idRaw.toIntOrNull()
            ?: throw RowProcessingException(ErrorKind.PARSE, "Некорректный идентификатор '$idRaw'")

        val type = ProductType.fromCsv(typeRaw)
            ?: throw RowProcessingException(ErrorKind.PARSE, "Неизвестный тип товара '$typeRaw'")

        val vehicle = VehicleType.fromCsv(vehicleRaw)
            ?: throw RowProcessingException(ErrorKind.PARSE, "Неизвестный тип автомобиля '$vehicleRaw'")

        val parsedPrice = try {
            PriceParser.parse(priceRaw)
        } catch (ex: PriceParseException) {
            throw RowProcessingException(ErrorKind.PARSE, ex.message ?: "Не удалось распарсить цену")
        }
        val priceRub = PriceParser.toRubles(parsedPrice)

        val parsedWeight = try {
            WeightParser.parse(weightRaw)
        } catch (ex: WeightParseException) {
            throw RowProcessingException(ErrorKind.PARSE, ex.message ?: "Не удалось распарсить вес")
        }
        val weightKg = WeightParser.toKilograms(parsedWeight)

        val extras = try {
            ExtraParser.parse(extraRaw, type)
        } catch (ex: ExtraParseException) {
            throw RowProcessingException(ErrorKind.PARSE, ex.message ?: "Некорректное поле extra")
        }

        if (name.isEmpty()) {
            throw RowProcessingException(ErrorKind.VALIDATION, "Пустое название товара")
        }
        if (parsedPrice.amount < 0) {
            throw RowProcessingException(ErrorKind.VALIDATION, "Цена не может быть отрицательной")
        }
        if (parsedPrice.amount == 0.0) {
            throw RowProcessingException(ErrorKind.VALIDATION, "Цена должна быть положительной")
        }
        if (parsedWeight.amount < 0) {
            throw RowProcessingException(ErrorKind.VALIDATION, "Вес не может быть отрицательным")
        }
        if (parsedWeight.amount == 0.0) {
            throw RowProcessingException(ErrorKind.VALIDATION, "Вес должен быть положительным")
        }

        return when (type) {
            ProductType.OIL -> Oil(
                id = id,
                name = name,
                priceRub = priceRub,
                weightKg = weightKg,
                vehicle = vehicle,
                volume = parseOptionalDouble(extras[ExtraSchema.VOLUME], "Объем"),
            )
            ProductType.TIRE -> Tire(
                id = id,
                name = name,
                priceRub = priceRub,
                weightKg = weightKg,
                vehicle = vehicle,
                diameter = parseOptionalDouble(extras[ExtraSchema.DIAMETER], "Диаметр"),
                season = parseSeason(extras[ExtraSchema.SEASON]),
            )
            ProductType.ACCESSORY -> Accessory(
                id = id,
                name = name,
                priceRub = priceRub,
                weightKg = weightKg,
                vehicle = vehicle,
                material = extras[ExtraSchema.MATERIAL],
                color = parseColor(extras[ExtraSchema.COLOR]),
            )
        }
    }

    private fun parseOptionalDouble(raw: String?, label: String): Double? {
        if (raw == null) return null
        return raw.toDoubleOrNull()
            ?: throw RowProcessingException(ErrorKind.PARSE, "$label '$raw' не является числом")
    }

    private fun parseSeason(raw: String?): String? {
        if (raw == null) return null
        if (raw !in ExtraSchema.allowedSeasons) {
            throw RowProcessingException(ErrorKind.VALIDATION, "Недопустимое значение 'season=$raw'")
        }
        return raw
    }

    private fun parseColor(raw: String?): String? {
        if (raw == null) return null
        if (raw !in ExtraSchema.allowedColors) {
            throw RowProcessingException(ErrorKind.VALIDATION, "Недопустимое значение 'color=$raw'")
        }
        return raw
    }
}
