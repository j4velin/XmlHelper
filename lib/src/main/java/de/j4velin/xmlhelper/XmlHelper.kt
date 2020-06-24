package de.j4velin.xmlhelper

import org.xmlpull.v1.XmlPullParser
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

/**
 * Reads the tag, to which the given parser points and created the corresponding XmlElement
 */
internal fun readTag(parser: XmlPullParser): XmlElement {
    if (parser.depth == 0) {
        parser.next()
    }
    if (parser.eventType != XmlPullParser.START_TAG) {
        throw IllegalArgumentException("Not a start tag: ${parser.eventType}")
    }

    val name = parser.name
    val attributes = HashMap<String, String>(parser.attributeCount)
    for (i in 0 until parser.attributeCount) {
        attributes[parser.getAttributeName(i)] = parser.getAttributeValue(i)
    }
    // move to next element (=value of the current tag)
    parser.next()
    val element = when (val token = skipWhitespace(parser)) {
        XmlPullParser.TEXT -> XmlPrimitive(name, attributes, parser.text)
        XmlPullParser.END_TAG -> XmlPrimitive(name, attributes, null)
        XmlPullParser.START_TAG -> {
            val elements = mutableListOf<XmlElement>()
            do {
                elements.add(readTag(parser))
                parser.next()
            } while (skipWhitespace(parser) != XmlPullParser.END_TAG)

            // all enclosed tags have a different name? -> map/object
            if (elements.map { it.name }.distinct().size == elements.size) {
                XmlObject(name, attributes, elements.map { it.name to it }.toMap())
            } else {
                XmlListImpl(name, attributes, elements)
            }
        }
        else -> throw IllegalArgumentException("Unexpected type: $token")
    }
    // skip until end tag
    while (parser.eventType != XmlPullParser.END_TAG) {
        parser.next()
    }
    // then skip the end tag
    parser.next()

    return element
}

@RequiresOptIn(
    message = "This feature is experimental and only works for selected data types",
    level = RequiresOptIn.Level.WARNING
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class ToDataClass

/**
 * Tries to construct an instance of the data class 'c' with the data given in 'xml'.
 *
 * This method only works if 'c' is a Kotlin data class with properties being primitives, other data
 * classes or of type List<String>. Options to provide custom deserializer code for other types might
 * be added at some point.
 */
@ToDataClass
fun <T : Any> toDataClass(c: KClass<T>, xml: XmlElement): T {
    if (!c.isData)
        throw java.lang.IllegalArgumentException(
            "Only data classes are supported in this version. Problematic class: ${c.qualifiedName}"
        )
    val parameterList = c.primaryConstructor!!.parameters
    val ctorValuesMap = HashMap<KParameter, Any?>(parameterList.size)

    for (parameter in parameterList) {
        val name = parameter.name
        val classifier = parameter.type.classifier as KClass<*>
        if (xml is XmlObject && xml.containsKey(name)) {
            ctorValuesMap[parameter] = getObject(classifier, xml[name]!!)
        } else if (xml.attributes.containsKey(name)) {
            ctorValuesMap[parameter] = getPrimitive(classifier, xml.attributes[name])
        }
    }

    return c.primaryConstructor!!.callBy(ctorValuesMap)

}

/**
 * Converts the given string into to given primitive class object
 */
private fun getPrimitive(c: KClass<*>, value: String?) = when (c) {
    String::class -> value.toString()
    Boolean::class -> value?.toBoolean()
    Byte::class -> value?.toByte()
    Short::class -> value?.toShort()
    Int::class -> value?.toInt()
    Long::class -> value?.toLong()
    Float::class -> value?.toFloat()
    Double::class -> value?.toDouble()
    else -> throw IllegalArgumentException("Unknown primitive type: ${c.qualifiedName}")
}

/**
 * Tries to convert the given XML element into the given class. Should work for data classes,
 * primitives and list of strings
 */
@ToDataClass
private fun getObject(c: KClass<*>, xml: XmlElement) =
    when (xml) {
        is XmlObject -> toDataClass(c, xml)
        is XmlPrimitive -> {
            if (c == String::class || c.javaPrimitiveType != null) {
                getPrimitive(c, xml.value)
            } else {
                toDataClass(c, xml)
            }
        }
        is XmlList -> xml.elements.filterIsInstance<XmlPrimitive>().map { it.value }
    }

/**
 * Skips all whitespaces and returns the next non-whitespace token
 */
private fun skipWhitespace(parser: XmlPullParser): Int {
    while (parser.eventType == XmlPullParser.TEXT && parser.text.isBlank()) {
        parser.next()
    }
    return parser.eventType
}