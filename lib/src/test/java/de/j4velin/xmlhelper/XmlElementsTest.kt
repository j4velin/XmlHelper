package de.j4velin.xmlhelper

import org.junit.Assert.*
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
            assertEquals(3, (list as XmlList).size)
            for (primitive in list) {
                assertTrue(primitive is XmlPrimitive)
                assertEquals("myPrimitive", primitive.name)
            }
            assertEquals("foo", (list[0] as XmlPrimitive).value)
            assertEquals("bar", (list[1] as XmlPrimitive).value)
            assertEquals("foobar", (list[2] as XmlPrimitive).value)
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
            assertEquals(3, (list as XmlList).size)
            for (primitive in list) {
                assertTrue(primitive is XmlPrimitive)
                assertEquals("myPrimitive", primitive.name)
            }
            assertNull((list[0] as XmlPrimitive).value)
            assertNull((list[1] as XmlPrimitive).value)
            assertNull((list[2] as XmlPrimitive).value)
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
            assertEquals(3, (list as XmlList).size)
            for (innerList in list) {
                assertTrue(innerList is XmlList)
                assertEquals("myInnerList", innerList.name)
                assertEquals(2, (innerList as XmlList).size)
            }
            val primitive00 = (list[0] as XmlList)[0] as XmlPrimitive
            assertEquals("foo", primitive00.value)
            val primitive01 = (list[0] as XmlList)[1] as XmlPrimitive
            assertEquals("bar", primitive01.value)

            val primitive10 = (list[1] as XmlList)[0] as XmlPrimitive
            assertEquals("foo2", primitive10.value)
            val primitive11 = (list[1] as XmlList)[1] as XmlPrimitive
            assertEquals("bar2", primitive11.value)

            val primitive20 = (list[2] as XmlList)[0] as XmlPrimitive
            assertEquals("foo3", primitive20.value)
            val primitive21 = (list[2] as XmlList)[1] as XmlPrimitive
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
            assertEquals(2, (obj as XmlObject).size)
            for (primitive in obj.values) {
                assertTrue(primitive is XmlPrimitive)
            }
            assertEquals("foobar", (obj["myStringPrimitive"] as XmlPrimitive).value)
            assertNull((obj["myEmptyPrimitive"] as XmlPrimitive).value)
        }
    }


    @Test
    fun objectOfObjects() {
        val file = File(javaClass.classLoader!!.getResource("objectOfObjects.xml").file)
        FileInputStream(file).use {
            val obj = XmlElement.fromInputStream(it)
            assertTrue(obj is XmlObject)
            assertEquals(4, (obj as XmlObject).size)

            assertTrue(obj["myPrimitive"] is XmlPrimitive)
            assertEquals("foobar", (obj["myPrimitive"] as XmlPrimitive).value)

            assertTrue(obj["myInnerObject"] is XmlObject)
            assertEquals(3, (obj["myInnerObject"] as XmlObject).size)

            assertTrue(obj["myList"] is XmlList)
            assertEquals(2, (obj["myList"] as XmlList).size)

            assertTrue(obj["myEmpty"] is XmlPrimitive)
            assertNull((obj["myEmpty"] as XmlPrimitive).value)
        }
    }
}