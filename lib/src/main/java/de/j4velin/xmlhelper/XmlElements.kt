package de.j4velin.xmlhelper

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

/**
 * Base class for all XML element types
 */
sealed class XmlElement(open val name: String, open val attributes: Map<String, String>) {
    companion object {
        /**
         * Creates a XmlEntity from an inputstream
         */
        fun fromInputStream(stream: InputStream) = stream.use { fromReader(InputStreamReader(it)) }

        /**
         * Creates a XmlEntity from a reader
         */
        fun fromReader(reader: Reader) = reader.use {
            val parser: XmlPullParser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it)
            readTag(parser)
        }
    }
}

/**
 * A XML primitive, e.g. a tag enclosing only a single value
 */
data class XmlPrimitive(
    override val name: String,
    override val attributes: Map<String, String>,
    val value: String?
) : XmlElement(name, attributes)

/**
 * Special case of a XmlContainer where all enclosing tags have a different name
 */
data class XmlObject(
    override val name: String,
    override val attributes: Map<String, String>,
    val map: Map<String, XmlElement>
) : XmlList(name, attributes, map.values.toList()), Map<String, XmlElement> by map {
    override fun isEmpty() = map.isEmpty()
    override val size = map.size
}

/**
 * A XML list/array, e.g. a tag enclosing multiple other tags
 */
internal data class XmlListImpl(
    override val name: String,
    override val attributes: Map<String, String>,
    override val elements: List<XmlElement>
) : XmlList(name, attributes, elements)

/**
 * Workaround for not being able to subclass a Kotlin data class
 */
sealed class XmlList(
    override val name: String,
    override val attributes: Map<String, String>,
    open val elements: List<XmlElement>
) : XmlElement(name, attributes), List<XmlElement> by elements