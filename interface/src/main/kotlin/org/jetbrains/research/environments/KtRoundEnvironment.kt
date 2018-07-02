package org.jetbrains.research.environments

import kotlinx.metadata.ClassName
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.asQualifiedIdentificator
import org.jetbrains.research.elements.*
import org.jetbrains.research.kotlinClass
import org.jetbrains.research.qualifiedIdentificator
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element

class KtRoundEnvironment(private val roundEnvironment: RoundEnvironment) : KtEnvironment {
    private val classElements = HashMap<String, KtClassElement<*>>()

    override fun getKtClassElement(javaElement: Element): KtClassElement<*>? {
        val identificator = javaElement.qualifiedIdentificator
        val storedElement = classElements[identificator]
        if (storedElement != null) return storedElement
        val classHeader = javaElement.kotlinClass()
        val metadata = classHeader?.let { KotlinClassMetadata.read(it) } ?: return null
        val classElement = when (metadata) {
            is KotlinClassMetadata.Class -> KtClass(this, javaElement, metadata)
            is KotlinClassMetadata.FileFacade -> KtFileFacade(this, javaElement, metadata)
            is KotlinClassMetadata.SyntheticClass -> KtSyntheticClass(this, javaElement, metadata)
            is KotlinClassMetadata.MultiFileClassFacade -> KtMultiFileClassFacade(this, javaElement, metadata)
            is KotlinClassMetadata.MultiFileClassPart -> KtMultiFileClassPart(this, javaElement, metadata)
            is KotlinClassMetadata.Unknown -> KtUnknown(this, javaElement, metadata)
        }
        classElements[identificator] = classElement
        return classElement
    }

    override fun getKtClassElement(name: ClassName): KtClassElement<*>? {
        val identificator = name.asQualifiedIdentificator
        classElements[identificator]?.let { return it }
        return findAllElements()
            .filter { it.qualifiedIdentificator == identificator }
            .mapNotNull { getKtClassElement(it) }
            .firstOrNull()
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