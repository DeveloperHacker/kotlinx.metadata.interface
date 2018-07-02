package org.jetbrains.research

import kotlinx.metadata.ClassName
import kotlinx.metadata.jvm.KotlinClassHeader
import javax.lang.model.element.*

inline fun <reified T> List<*>.allIsInstance() = filterIsInstance<T>().let {
    if (it.size == size) it else null
}

fun AnnotationMirror.isKotlinMetadata() = (annotationType.asElement() as? TypeElement)
    ?.qualifiedName
    ?.contentEquals("kotlin.Metadata")
        ?: false

fun AnnotationValue?.asInt() = this?.value as? Int

fun AnnotationValue?.asString() = this?.value as? String

fun AnnotationValue?.asArray() = (this?.value as? List<*>)?.allIsInstance<AnnotationValue>()

fun List<Int>.toIntArray() = IntArray(size) { get(it) }

fun Element.kotlinClass() = annotationMirrors.firstOrNull { it.isKotlinMetadata() }?.let {
    val elements = it.elementValues.map { it.key.simpleName.toString() to it.value }.toMap()
    val kind = elements["k"].asInt()
    val metadataVersion = elements["mv"].asArray()?.mapNotNull { it.asInt() }?.toIntArray()
    val bytecodeVersion = elements["bv"].asArray()?.mapNotNull { it.asInt() }?.toIntArray()
    val data1 = elements["d1"].asArray()?.mapNotNull { it.asString() }?.toTypedArray()
    val data2 = elements["d2"].asArray()?.mapNotNull { it.asString() }?.toTypedArray()
    val extraString = elements["xs"].asString()
    val packageName = elements["pn"].asString()
    val extraInt = elements["xi"].asInt()
    KotlinClassHeader(kind, metadataVersion, bytecodeVersion, data1, data2, extraString, packageName, extraInt)
}

val Element.qualifiedIdentificator: String
    get() = when (kind) {
        ElementKind.PACKAGE, ElementKind.CLASS -> "[$kind][$this]"
        else -> "[$kind][${enclosingElement?.qualifiedIdentificator}][$this]"
    }

val ClassName.asQualifiedIdentificator: String
    get() = "[${ElementKind.CLASS}][$this]"

data class KtWrapper<T>(val value: T)