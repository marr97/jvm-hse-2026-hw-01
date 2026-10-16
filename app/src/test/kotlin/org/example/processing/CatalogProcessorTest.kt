package org.example.processing

import org.example.model.Accessory
import org.example.model.ErrorKind
import org.example.model.Oil
import org.example.model.Tire
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CatalogProcessorTest {
    private val processor = CatalogProcessor()

    private val sampleCsv = """
        id,name,type,price,weight,vehicle,extra
        1,"Mobil 1, Synthetic",oil,$12.5,10lb,PASSENGER,viscosity=5W-30;volume=4
        2,Castrol Magnatec,oil,300.5руб,1.2kg,PASSENGER,viscosity=10W-40;volume=1
        3,"Michelin Primacy",tire,15$,0.3kg,PASSENGER,diameter=16;season=SUMMER
        4,Nokian Hakkapeliitta,tire,25руб,2lb,TRUCK,diameter=18;season=WINTER
        5,Щетки стеклоочистителя,accessory,100руб,0.1kg,PASSENGER,material=резина;color=черный
        6,"Bosch, Air Filter",accessory,$7.5,0.05kg,MOTORCYCLE,material=бумага;color=белый
        7,Масло,oil,abc,0.5kg,PASSENGER,viscosity=5W-30
        8,,tire,10$,0.2kg,PASSENGER,diameter=16
        9,Присадка,accessory,50руб,0.1kg,PASSENGER,material=пластик;color=red
        10,Шина,tire,-5$,0.3kg,PASSENGER,diameter=17
        11,Масло,oil,100руб,0.5kg,PASSENGER,viscosity=5W-30;volume=abc
        12,Аккумулятор,battery,200руб,0.4kg,PASSENGER,material=свинец
    """.trimIndent()

    @Test
    fun `processes sample catalog with expected statistics`() {
        val result = processor.process(sampleCsv)
        assertEquals(12, result.processedRows)
        assertEquals(6, result.products.size)
        assertEquals(3, result.parseErrorCount)
        assertEquals(3, result.validationErrorCount)
    }

    @Test
    fun `normalizes first oil product`() {
        val oil = processor.process(sampleCsv).products.first()
        assertIs<Oil>(oil)
        assertEquals(1, oil.id)
        assertEquals("Mobil 1, Synthetic", oil.name)
        assertEquals(1125.0, oil.priceRub)
        assertEquals(4.54, oil.weightKg)
        assertEquals(4.0, oil.volume)
    }

    @Test
    fun `maps tire and accessory extras`() {
        val products = processor.process(sampleCsv).products
        val tire = products.filterIsInstance<Tire>().first()
        assertEquals(16.0, tire.diameter)
        assertEquals("SUMMER", tire.season)

        val accessory = products.filterIsInstance<Accessory>().first()
        assertEquals("резина", accessory.material)
        assertEquals("черный", accessory.color)
    }

    @Test
    fun `collects expected error messages`() {
        val messages = processor.process(sampleCsv).errors.associate { it.lineNumber to it }
        assertEquals(ErrorKind.PARSE, messages[7]?.kind)
        assertEquals("Не удалось распарсить цену 'abc'", messages[7]?.message)
        assertEquals(ErrorKind.VALIDATION, messages[8]?.kind)
        assertEquals("Пустое название товара", messages[8]?.message)
        assertEquals("Недопустимое значение 'color=red'", messages[9]?.message)
        assertEquals("Цена не может быть отрицательной", messages[10]?.message)
        assertEquals("Объем 'abc' не является числом", messages[11]?.message)
        assertEquals("Неизвестный тип товара 'battery'", messages[12]?.message)
    }

    @Test
    fun `ignores unknown extra keys`() {
        val csv = """
            id,name,type,price,weight,vehicle,extra
            1,Oil,oil,100руб,1kg,PASSENGER,viscosity=5W-30;volume=2
        """.trimIndent()
        val oil = processor.process(csv).products.single() as Oil
        assertEquals(2.0, oil.volume)
    }

    @Test
    fun `accepts valid data`() {
        val csv = """
            id,name,type,price,weight,vehicle,extra
            1,Good,oil,10руб,1kg,PASSENGER,volume=1
        """.trimIndent()
        val result = processor.process(csv)
        assertEquals(1, result.products.size)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `rejects empty name as validation`() {
        val csv = """
            id,name,type,price,weight,vehicle,extra
            1,,oil,10руб,1kg,PASSENGER,volume=1
        """.trimIndent()
        val error = processor.process(csv).errors.single()
        assertEquals(ErrorKind.VALIDATION, error.kind)
    }
}
