package de.j4velin.xmlhelper

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.io.StringReader

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
            assertNull(primitive.value)
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
            assertNull(primitive.value)
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
            assertEquals("foo", list[0].value)
            assertEquals("bar", list[1].value)
            assertEquals("foobar", list[2].value)
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
            assertNull(list[0].value)
            assertNull(list[1].value)
            assertNull(list[2].value)
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

            assertEquals("foo", (list[0] as XmlList)[0].value)
            assertEquals("bar", (list[0] as XmlList)[1].value)

            assertEquals("foo2", (list[1] as XmlList)[0].value)
            assertEquals("bar2", (list[1] as XmlList)[1].value)

            assertEquals("foo3", (list[2] as XmlList)[0].value)
            assertEquals("bar3", (list[2] as XmlList)[1].value)
        }
    }

    @Test
    fun objectOfPrimitives() {
        val file = File(javaClass.classLoader!!.getResource("objectOfPrimitives.xml").file)
        FileInputStream(file).use {
            val obj = XmlElement.fromInputStream(it)
            assertTrue(obj is XmlObject)
            assertEquals("myObject", obj.name)
            assertTrue(obj.attributes.isEmpty())
            assertEquals(2, (obj as XmlObject).size)
            for (primitive in obj.values) {
                assertTrue(primitive is XmlPrimitive)
            }
            assertEquals("foobar", obj["myStringPrimitive"]?.value)
            assertNull(obj["myEmptyPrimitive"]?.value)
        }
    }

    @Test
    fun fromReader() {
        val xml = """
            <someTag someAttribute="foobar">
                <somePrimitive>primitiveValue</somePrimitive>
                <withoutValue />
                <stringList foo="bar">
                    <stringElement>hello</stringElement>
                    <stringElement>world</stringElement>
                </stringList>
            </someTag>
            """
        val xmlObject = XmlElement.fromReader(StringReader(xml)) as XmlObject

        assertEquals("someTag", xmlObject.name)
        assertEquals("foobar", xmlObject.attributes["someAttribute"])

        val xmlPrimitive = xmlObject["somePrimitive"] as XmlPrimitive
        assertEquals("primitiveValue", xmlPrimitive.value)

        assertNull(xmlObject["withoutValue"]?.value)

        val xmlList = xmlObject["stringList"] as XmlList
        assertEquals(2, xmlList.size)
        assertEquals("hello", xmlList[0].value)
        assertEquals("world", xmlList[1].value)
    }

    @Test
    fun xmlObjectIsAlsoAList() {
        val file = File(javaClass.classLoader!!.getResource("objectOfPrimitives.xml").file)
        FileInputStream(file).use {
            val obj = XmlElement.fromInputStream(it)
            assertTrue(obj is XmlList)
            assertTrue(obj is XmlObject)
            // smartcast not working on first call?
            assertEquals((obj as XmlObject)["myStringPrimitive"], obj[0])
            assertEquals(obj["myEmptyPrimitive"], obj[1])
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
            assertEquals("foobar", obj["myPrimitive"]?.value)

            assertTrue(obj["myInnerObject"] is XmlObject)
            assertEquals(3, (obj["myInnerObject"] as XmlObject).size)

            assertTrue(obj["myList"] is XmlList)
            assertEquals(2, (obj["myList"] as XmlList).size)

            assertTrue(obj["myEmpty"] is XmlPrimitive)
            assertNull(obj["myEmpty"]?.value)
        }
    }

    @Test
    fun listToObject() {
        val file = File(javaClass.classLoader!!.getResource("listOfPrimitives.xml").file)
        FileInputStream(file).use {
            val list = XmlElement.fromInputStream(it)
            assertTrue(list is XmlList)
            assertTrue(list is XmlListImpl)

            val obj = (list as XmlList).asXmlObject()

            assertTrue(obj is XmlObject)
            assertEquals(1, obj.size)
            assertNotNull(obj["myPrimitive"])
            assertTrue(obj["myPrimitive"] is XmlPrimitive)
        }
    }
}