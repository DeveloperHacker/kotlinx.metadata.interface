package org.jetbrains.research.impl

import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.elements.KtClass
import org.jetbrains.research.elements.KtClassElement
import org.jetbrains.research.elements.KtElement
import org.jetbrains.research.elements.KtEnvironment
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

class KtEnvironmentImpl(val roundEnvironment: RoundEnvironment) : KtEnvironment {

    private val elements = IdentityHashMap<Element, KtElement>()

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

    private fun Element.ktClassElement(): KtClassElement<*>? {
        val classHeader = kotlinClass() ?: return null
        val metadata = KotlinClassMetadata.read(classHeader) ?: return null
        val parent = { getKtElement(enclosingElement) }
        return when (metadata) {
            is KotlinClassMetadata.Class -> KtClassImpl(this@KtEnvironmentImpl, this, metadata, parent)
            is KotlinClassMetadata.FileFacade -> KtFileFacadeImpl(this@KtEnvironmentImpl, this, metadata, parent)
            is KotlinClassMetadata.SyntheticClass -> KtSyntheticClassImpl(this@KtEnvironmentImpl, this, metadata, parent)
            is KotlinClassMetadata.MultiFileClassFacade -> KtMultiFileClassFacadeImpl(this, metadata, parent)
            is KotlinClassMetadata.MultiFileClassPart -> KtMultiFileClassPartImpl(this@KtEnvironmentImpl, this, metadata, parent)
            is KotlinClassMetadata.Unknown -> KtUnknownImpl(this, metadata, parent)
        }
    }

    fun Element.getNearestClassElement(): KtClassElement<*>? {
        var current: Element? = this
        while (current != null) {
            val classElement = current.ktClassElement()
            if (classElement != null) return classElement
            current = current.enclosingElement
        }
        return null
    }

    override fun getKtElement(javaElement: Element): KtElement? {
        val storedElement = elements[javaElement]
        if (storedElement != null) return storedElement
        val classElement = javaElement.getNearestClassElement() ?: return null
        System.err.println((classElement as KtClass).functions.first().javaElement)
        cache(classElement)
        return elements[javaElement]
    }

    override fun cache(ktElement: KtElement): Boolean {
        val element = ktElement.javaElement
        if (elements.containsKey(element)) return false
        elements[element] = ktElement
        return true
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

    override fun getAllKtElements() = findAllElements().mapNotNull { getKtElement(it) }

    override fun getRootKtElements() =
        roundEnvironment.rootElements.asSequence().mapNotNull { getKtElement(it) }

    override fun <T : Annotation> getKtElements(annotationType: Class<T>) =
        roundEnvironment.getElementsAnnotatedWith(annotationType).asSequence().mapNotNull { getKtElement(it) }
}