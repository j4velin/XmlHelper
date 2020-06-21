# XmlHelper

[![Build Status](https://travis-ci.org/j4velin/XmlHelper.svg?branch=master)](https://travis-ci.org/j4velin/XmlHelper)


Setup
-----

Add to your build.gradle:
```
dependencies {
    compile 'de.j4velin.XmlHelper:lib:+'
}
```
For non-Android usage, you'll also need to add an implementation of the [XML Pull Parsing API](http://www.xmlpull.org/impls.shtml)


Usage
-----

```
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

assertNull((xmlObject["withoutValue"] as XmlPrimitive).value)

val xmlList = xmlObject["stringList"] as XmlList
assertEquals(2, xmlList.size)
assertEquals("hello", (xmlList[0] as XmlPrimitive).value)
assertEquals("world", (xmlList[1] as XmlPrimitive).value)
```