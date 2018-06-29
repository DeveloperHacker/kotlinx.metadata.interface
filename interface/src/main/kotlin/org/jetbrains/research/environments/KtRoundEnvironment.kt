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
    private val classElements = HashMap<String, KtClassElement>()

    override fun getKtClassElement(javaElement: Element): KtClassElement? {
        val identificator = javaElement.qualifiedIdentificator
        val storedElement = classElements[identificator]
        if (storedElement != null) return storedElement
        val classHeader = javaElement.kotlinClass()
        val classElementMetadata = classHeader?.let { KotlinClassMetadata.read(it) } ?: return null
        val classElement = when (classElementMetadata) {
            is KotlinClassMetadata.Class -> KtClass(this, javaElement, classElementMetadata)
            is KotlinClassMetadata.FileFacade -> KtFileFacade(javaElement)
            is KotlinClassMetadata.SyntheticClass -> KtSyntheticClass(javaElement)
            is KotlinClassMetadata.MultiFileClassFacade -> KtMultiFileClassFacade(javaElement)
            is KotlinClassMetadata.MultiFileClassPart -> KtMultiFileClassPart(javaElement)
            is KotlinClassMetadata.Unknown -> KtUnknown(javaElement)
        }
        classElements[identificator] = classElement
        return classElement
    }

    override fun getKtClass(javaElement: Element) = getKtClassElement(javaElement) as KtClass?

    override fun getKtClassElement(name: ClassName): KtClassElement? {
        val identificator = name.asQualifiedIdentificator
        classElements[identificator]?.let { return it }
        return findAllElements()
                .filter { it.qualifiedIdentificator == identificator }
                .mapNotNull { getKtClassElement(it) }
                .firstOrNull()
    }

    override fun getKtClass(name: ClassName) = getKtClassElement(name) as? KtClass

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

    override fun findAllKtClasses() = findAllKtClassElements().filterIsInstance<KtClass>()

    override fun getRootClassElements() =
            roundEnvironment.rootElements.mapNotNull { getKtClassElement(it) }

    override fun <T : Annotation> getClassElementsWithAnnotation(annotationType: Class<T>) =
            roundEnvironment.getElementsAnnotatedWith(annotationType).mapNotNull { getKtClassElement(it) }
}