package de.j4velin.xmlhelper

import junit.framework.Assert.*
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class XmlElementsTest {

    @Test
    fun primitive() {
        val file = File(javaClass.classLoader!!.getResource("primitive.xml").file)
        FileInputStream(file).use {
            val primitive = XmlElement.fromInputStream(it)
            assertTrue(primitive is XmlPrimitive)
            assertEquals("myTag", primitive.name)
            assertEquals("myValue", (primitive as XmlPrimitive).value)
            assertTrue(primitive.attributes.isEmpty())
        }
    }

    @Test
    fun emptyPrimitive() {
        val file = File(javaClass.classLoader!!.getResource("emptyPrimitive.xml").file)
        FileInputStream(file).use {
            val primitive = XmlElement.fromInputStream(it)
            assertTrue(primitive is XmlPrimitive)
            assertEquals("myTag", primitive.name)
            assertNull((primitive as XmlPrimitive).value)
            assertTrue(primitive.attributes.isEmpty())
        }
    }

    @Test
    fun emptyPrimitive2() {
        val file = File(javaClass.classLoader!!.getResource("emptyPrimitive2.xml").file)
        FileInputStream(file).use {
            val primitive = XmlElement.fromInputStream(it)
            assertTrue(primitive is XmlPrimitive)
            assertEquals("myTag", primitive.name)
            assertNull((primitive as XmlPrimitive).value)
            assertTrue(primitive.attributes.isEmpty())
        }
    }

    @Test
    fun primitiveWithAttributes() {
        val file = File(javaClass.classLoader!!.getResource("primitiveWithAttributes.xml").file)
        FileInputStream(file).use {
            val primitive = XmlElement.fromInputStream(it) as XmlPrimitive
            assertEquals(2, primitive.attributes.size)
            assertTrue(primitive.attributes.containsKey("attribute1"))
            assertTrue(primitive.attributes.containsKey("attribute2"))
            assertEquals("foo", primitive.attributes["attribute1"])
            assertEquals("bar", primitive.attributes["attribute2"])
        }
    }

    @Test
    fun listOfPrimitives() {
        val file = File(javaClass.classLoader!!.getResource("listOfPrimitives.xml").file)
        FileInputStream(file).use {
            val list = XmlElement.fromInputStream(it)
            assertTrue(list is XmlList)
            assertEquals("myList", list.name)
            assertTrue(list.attributes.isEmpty())
            assertEquals(3, (list as XmlList).values.size)
            for (primitive in list.values) {
                assertTrue(primitive is XmlPrimitive)
                assertEquals("myPrimitive", primitive.name)
            }
            assertEquals("foo", (list.values[0] as XmlPrimitive).value)
            assertEquals("bar", (list.values[1] as XmlPrimitive).value)
            assertEquals("foobar", (list.values[2] as XmlPrimitive).value)
        }
    }

    @Test
    fun listOfEmptyPrimitives() {
        val file = File(javaClass.classLoader!!.getResource("listOfEmptyPrimitives.xml").file)
        FileInputStream(file).use {
            val list = XmlElement.fromInputStream(it)
            assertTrue(list is XmlList)
            assertEquals("myList", list.name)
            assertTrue(list.attributes.isEmpty())
            assertEquals(3, (list as XmlList).values.size)
            for (primitive in list.values) {
                assertTrue(primitive is XmlPrimitive)
                assertEquals("myPrimitive", primitive.name)
            }
            assertNull((list.values[0] as XmlPrimitive).value)
            assertNull((list.values[1] as XmlPrimitive).value)
            assertNull((list.values[2] as XmlPrimitive).value)
        }
    }

    @Test
    fun listOfLists() {
        val file = File(javaClass.classLoader!!.getResource("listOfLists.xml").file)
        FileInputStream(file).use {
            val list = XmlElement.fromInputStream(it)
            assertTrue(list is XmlList)
            assertEquals("myList", list.name)
            assertTrue(list.attributes.isEmpty())
            assertEquals(3, (list as XmlList).values.size)
            for (list in list.values) {
                assertTrue(list is XmlList)
                assertEquals("myInnerList", list.name)
                assertEquals(2, (list as XmlList).values.size)
            }
            val primitive00 = (list.values[0] as XmlList).values[0] as XmlPrimitive
            assertEquals("foo", primitive00.value)
            val primitive01 = (list.values[0] as XmlList).values[1] as XmlPrimitive
            assertEquals("bar", primitive01.value)

            val primitive10 = (list.values[1] as XmlList).values[0] as XmlPrimitive
            assertEquals("foo2", primitive10.value)
            val primitive11 = (list.values[1] as XmlList).values[1] as XmlPrimitive
            assertEquals("bar2", primitive11.value)

            val primitive20 = (list.values[2] as XmlList).values[0] as XmlPrimitive
            assertEquals("foo3", primitive20.value)
            val primitive21 = (list.values[2] as XmlList).values[1] as XmlPrimitive
            assertEquals("bar3", primitive21.value)
        }
    }

    @Test
    fun objectOfPrimitives() {
        val file = File(javaClass.classLoader!!.getResource("objectOfPrimitives.xml").file)
        FileInputStream(file).use {
            val obj = XmlElement.fromInputStream(it)
            println(obj)
            assertTrue(obj is XmlObject)
            assertEquals("myObject", obj.name)
            assertTrue(obj.attributes.isEmpty())
            assertEquals(2, (obj as XmlObject).values.size)
            for (primitive in obj.values.values) {
                assertTrue(primitive is XmlPrimitive)
            }
            assertEquals("foobar", (obj.values["myStringPrimitive"] as XmlPrimitive).value)
            assertNull((obj.values["myEmptyPrimitive"] as XmlPrimitive).value)
        }
    }


    @Test
    fun objectOfObjects() {
        val file = File(javaClass.classLoader!!.getResource("objectOfObjects.xml").file)
        FileInputStream(file).use {
            val obj = XmlElement.fromInputStream(it)
            assertTrue(obj is XmlObject)
            assertEquals(4, (obj as XmlObject).values.size)

            assertTrue(obj.values["myPrimitive"] is XmlPrimitive)
            assertEquals("foobar", (obj.values["myPrimitive"] as XmlPrimitive).value)

            assertTrue(obj.values["myInnerObject"] is XmlObject)
            assertEquals(3, (obj.values["myInnerObject"] as XmlObject).values.size)

            assertTrue(obj.values["myList"] is XmlList)
            assertEquals(2, (obj.values["myList"] as XmlList).values.size)

            assertTrue(obj.values["myEmpty"] is XmlPrimitive)
            assertNull((obj.values["myEmpty"] as XmlPrimitive).value)
        }
    }
}