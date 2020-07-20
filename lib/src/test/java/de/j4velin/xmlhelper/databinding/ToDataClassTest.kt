package de.j4velin.xmlhelper.databinding

import de.j4velin.xmlhelper.XmlElement
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.StringReader

data class Car(
    val licensePlate: String,
    val tires: List<String>,
    val driver: Driver,
    val color: Int = 0x00FF00
)

data class Driver(val name: String, val age: Int)

@OptIn(ToDataClass::class)
class ToDataClassTest {
    @Test
    fun car() {
        val xml = """
            <car>
                <licensePlate>A-BC 4242</licensePlate>
                <driver>
                    <name>Max Mustermann</name>
                    <age>42</age>
                </driver>
                <tires>
                    <tire>frontLeft</tire>
                    <tire>frontRight</tire>
                    <tire>rearLeft</tire>
                    <tire>rearRight</tire>
                </tires>
            </car>
            """
        val xmlObject =
            XmlElement.fromReader(StringReader(xml))
        val car: Car = toDataClass(
            Car::class, xmlObject)

        assertEquals("A-BC 4242", car.licensePlate)
        assertEquals("Max Mustermann", car.driver.name)
        assertEquals(42, car.driver.age)
        assertEquals(4, car.tires.size)
    }

    @Test
    fun driverFromAttributes() {
        val xml = """<driver name="Max Mustermann" age="42" />"""
        val xmlObject =
            XmlElement.fromReader(StringReader(xml))
        val driver = toDataClass(Driver::class, xmlObject)
        assertEquals("Max Mustermann", driver.name)
        assertEquals(42, driver.age)
    }
}