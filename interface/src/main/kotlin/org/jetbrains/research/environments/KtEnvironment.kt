package org.jetbrains.research.environments

import kotlinx.metadata.ClassName
import org.jetbrains.research.elements.KtClassElement
import javax.lang.model.element.Element

interface KtEnvironment {
    fun getKtClassElement(javaElement: Element): KtClassElement<*>?

    fun getKtClassElement(name: ClassName): KtClassElement<*>?

    fun findAllKtClassElements(): Sequence<KtClassElement<*>>

    fun getRootClassElements(): Sequence<KtClassElement<*>>

    fun <T : Annotation> getClassElementsWithAnnotation(annotationType: Class<T>): Sequence<KtClassElement<*>>
}
