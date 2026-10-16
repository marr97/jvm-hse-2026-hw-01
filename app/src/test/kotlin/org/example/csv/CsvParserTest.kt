package org.example.csv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CsvParserTest {
    private val parser = CsvParser()

    @Test
    fun `parses simple comma-separated values`() {
        val fields = parser.parseLine("1,Castrol Magnatec,oil,300.5руб,1.2kg,PASSENGER,volume=1")
        assertEquals(7, fields.size)
        assertEquals("Castrol Magnatec", fields[1])
        assertEquals("volume=1", fields[6])
    }

    @Test
    fun `parses quoted field with comma`() {
        val fields = parser.parseLine("""1,"Mobil 1, Synthetic",oil,$12.5,10lb,PASSENGER,volume=4""")
        assertEquals("Mobil 1, Synthetic", fields[1])
        assertEquals("oil", fields[2])
    }

    @Test
    fun `parses doubled quotes inside quoted field`() {
        val fields = parser.parseLine("1,\"He said \"\"hello\"\"\",oil,1руб,1kg,PASSENGER,")
        assertEquals("He said \"hello\"", fields[1])
    }

    @Test
    fun `parseRecords skips header and empty lines`() {
        val csv = """
            id,name,type,price,weight,vehicle,extra

            1,A,oil,$1,1kg,PASSENGER,volume=1

            2,B,tire,2руб,2kg,TRUCK,diameter=16
        """.trimIndent()
        val records = parser.parseRecords(csv)
        assertEquals(2, records.size)
        assertEquals(1, records[0].lineNumber)
        assertEquals(2, records[1].lineNumber)
        assertEquals("A", records[0].fields[1])
    }

    @Test
    fun `unclosed quotes become a record error`() {
        val csv = "id,name\n1,\"broken"
        val records = parser.parseRecords(csv)
        assertEquals(1, records.size)
        assertEquals("Незакрытые кавычки в строке", records[0].error)
    }

    @Test
    fun `empty file throws`() {
        assertFailsWith<CsvParseException> { parser.parseRecords("   \n  ") }
    }
}
