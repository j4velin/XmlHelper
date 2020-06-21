package de.j4velin.xmlhelper

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Base class for all XML element types
 */
sealed class XmlElement(open val name: String, open val attributes: Map<String, String>) {
    companion object {
        /**
         * Creates a XmlEntity from an inputstream
         */
        fun fromInputStream(stream: InputStream): XmlElement =
            stream.use { inputStream ->
                val parser: XmlPullParser = XmlPullParserFactory.newInstance().newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                parser.setInput(InputStreamReader(inputStream))
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
 * A XML object, e.g. a tag enclosing multiple other tags
 */
data class XmlObject(
    override val name: String,
    override val attributes: Map<String, String>,
    val elements: Map<String, XmlElement>
) : XmlElement(name, attributes), Map<String, XmlElement> by elements

/**
 * A XML list/array. Special case of a XML object in which all enclosed tags have the same name
 */
data class XmlList(
    override val name: String,
    override val attributes: Map<String, String>,
    val elements: List<XmlElement>
) : XmlElement(name, attributes), List<XmlElement> by elements