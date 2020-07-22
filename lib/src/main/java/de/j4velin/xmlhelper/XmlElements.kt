/*
 * Copyright 2020 Thomas Hoffmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.j4velin.xmlhelper

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

/**
 * Base class for all XML element types
 *
 * @property name the name of the xml tag
 * @property attributes the attributes of the xml tag
 */
sealed class XmlElement(open val name: String, open val attributes: Map<String, String>) {
    companion object {
        /**
         * Creates a XmlEntity from an inputstream
         * @param stream the input stream to read from
         * @return the parsed XmlElement
         */
        @JvmStatic
        fun fromInputStream(stream: InputStream) = stream.use { fromReader(InputStreamReader(it)) }

        /**
         * Creates a XmlEntity from a xml parser
         * @param parser the xml parser to read from
         * @return the parsed XmlElement
         */
        @JvmStatic
        fun fromXmlParser(parser: XmlPullParser) = readTag(parser)

        /**
         * Creates a XmlEntity from a reader
         * @param reader the reader to read from
         * @return the parsed XmlElement
         */
        @JvmStatic
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
 *
 * @property value the value enclosed in this tag or null, if this tag does not enclose any value
 */
data class XmlPrimitive internal constructor(
    override val name: String,
    override val attributes: Map<String, String>,
    val value: String?
) : XmlElement(name, attributes)

/**
 * Special case of a XmlList where all enclosing tags have a different name. This allows accessing
 * an enclosed tag by its name, like a map.
 *
 * @property map mapping of the enclosed tags with key=tag name, value=XmlElement
 */
data class XmlObject internal constructor(
    override val name: String,
    override val attributes: Map<String, String>,
    private val map: Map<String, XmlElement>
) : XmlList(name, attributes, map.values.toList()), Map<String, XmlElement> by map {
    override fun isEmpty() = map.isEmpty()
    override val size = map.size
}

/**
 * Internal class as a workaround for Kotlin not allowing to extend data classes yet.
 */
internal data class XmlListImpl internal constructor(
    override val name: String,
    override val attributes: Map<String, String>,
    override val elements: List<XmlElement>
) : XmlList(name, attributes, elements)

/**
 * Class representing a "xml collection", e.g. a XML tag enclosing multiple other tags.
 *
 * @property elements the xml elements enclosed within this tag
 */
sealed class XmlList(
    override val name: String,
    override val attributes: Map<String, String>,
    internal open val elements: List<XmlElement>
) : XmlElement(name, attributes), List<XmlElement> by elements