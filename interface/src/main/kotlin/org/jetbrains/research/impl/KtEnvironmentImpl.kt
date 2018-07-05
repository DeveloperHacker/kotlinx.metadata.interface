package org.jetbrains.research.impl

import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.elements.KtClassElement
import org.jetbrains.research.elements.KtEnvironment
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.*

class KtEnvironmentImpl(private val roundEnvironment: RoundEnvironment) : KtEnvironment {
    private val classElements = HashMap<String, KtClassElement<*>>()

    private val Element.qualifiedIdentificator: String
        get() = when (kind) {
            ElementKind.PACKAGE, ElementKind.CLASS, ElementKind.ANNOTATION_TYPE -> "[$kind][$this]"
            else -> "[$kind][${enclosingElement?.qualifiedIdentificator}][$this]"
        }

    private inline fun <reified T> List<*>.allIsInstance() = filterIsInstance<T>().let {
        if (it.size == size) it else null
    }

    private fun AnnotationMirror.isKotlinMetadata() = (annotationType.asElement() as? TypeElement)
        ?.qualifiedName
        ?.contentEquals("kotlin.Metadata")
            ?: false

    private fun AnnotationValue?.asInt() = this?.value as? Int

    private fun AnnotationValue?.asString() = this?.value as? String

    private fun AnnotationValue?.asArray() = (this?.value as? List<*>)?.allIsInstance<AnnotationValue>()

    private fun List<Int>.toIntArray() = IntArray(size) { get(it) }

    private fun Element.kotlinClass() = annotationMirrors.firstOrNull { it.isKotlinMetadata() }?.let {
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

    override fun getKtClassElement(javaElement: Element): KtClassElement<*>? {
        val identificator = javaElement.qualifiedIdentificator
        val storedElement = classElements[identificator]
        if (storedElement != null) return storedElement
        val classHeader = javaElement.kotlinClass()
        val metadata = classHeader?.let { KotlinClassMetadata.read(it) } ?: return null
        val classElement = when (metadata) {
            is KotlinClassMetadata.Class -> KtClassImpl(this, javaElement, metadata)
            is KotlinClassMetadata.FileFacade -> KtFileFacadeImpl(this, javaElement, metadata)
            is KotlinClassMetadata.SyntheticClass -> KtSyntheticClassImpl(this, javaElement, metadata)
            is KotlinClassMetadata.MultiFileClassFacade -> KtMultiFileClassFacadeImpl(javaElement, metadata)
            is KotlinClassMetadata.MultiFileClassPart -> KtMultiFileClassPartImpl(this, javaElement, metadata)
            is KotlinClassMetadata.Unknown -> KtUnknownImpl(javaElement, metadata)
        }
        classElements[identificator] = classElement
        return classElement
    }

    private fun findAllElements() = Sequence {
        object : Iterator<Element> {
            val elements: Queue<Element> = LinkedList(roundEnvironment.rootElements)

            override fun hasNext() = elements.isNotEmpty()

            override fun next(): Element {
                val element = elements.remove()
                elements.addAll(element.enclosedElements)
                return element
            }
        }
    }

    override fun findAllKtClassElements() = findAllElements().mapNotNull { getKtClassElement(it) }

    override fun getRootClassElements() =
        roundEnvironment.rootElements.asSequence().mapNotNull { getKtClassElement(it) }

    override fun <T : Annotation> getClassElementsWithAnnotation(annotationType: Class<T>) =
        roundEnvironment.getElementsAnnotatedWith(annotationType).asSequence().mapNotNull { getKtClassElement(it) }
}