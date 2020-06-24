package de.j4velin.xmlhelper

import org.xmlpull.v1.XmlPullParser
import javax.xml.crypto.dsig.XMLObject
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

/**
 * Skips all whitespaces and returns the next non-whitespace token
 */
private fun skipWhitespace(parser: XmlPullParser): Int {
    while (parser.eventType == XmlPullParser.TEXT && parser.text.isBlank()) {
        parser.next()
    }
    return parser.eventType
}