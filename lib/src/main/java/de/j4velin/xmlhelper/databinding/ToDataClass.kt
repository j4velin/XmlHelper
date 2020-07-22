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
package de.j4velin.xmlhelper.databinding

import de.j4velin.xmlhelper.XmlElement
import de.j4velin.xmlhelper.XmlList
import de.j4velin.xmlhelper.XmlObject
import de.j4velin.xmlhelper.XmlPrimitive
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

@RequiresOptIn(
    message = "This feature is experimental and only works for selected data types",
    level = RequiresOptIn.Level.WARNING
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
/**
 * Annotates an experimental feature that only works for selected data types
 */
annotation class ToDataClass

/**
 * Tries to construct an instance of the data class 'c' with the data given in 'xml'.
 *
 * This method only works if 'c' is a Kotlin data class with properties being primitives, other data
 * classes or of type List<String>. Options to provide custom deserializer code for other types might
 * be added at some point.
 *
 * @param c the data class to serialize into
 * @param xml the xml source
 *
 * @return a kotlin data class representing the given xml element
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
 * Tries to convert the given XML element into the given class. Should work for data classes,
 * primitives and list of strings
 *
 * @param c the target class
 * @param xml the source xml data
 *
 * @return an object of type 'c' representing the data of 'xml'
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
 * Converts the given string into to given primitive class object
 *
 * @param c the primitive type
 * @param value the source string
 *
 * @return an instance of the 'c' class with the value given in 'value'
 */
private fun getPrimitive(c: KClass<*>, value: String?): Any? = when (c) {
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