package org.jetbrains.research.environments

import org.jetbrains.research.elements.KtClassElement
import javax.lang.model.element.Element

interface KtEnvironment {
    fun getKtClassElement(javaElement: Element): KtClassElement<*>?

    fun findAllKtClassElements(): Sequence<KtClassElement<*>>

    fun getRootClassElements(): Sequence<KtClassElement<*>>

    fun <T : Annotation> getClassElementsWithAnnotation(annotationType: Class<T>): Sequence<KtClassElement<*>>
}
