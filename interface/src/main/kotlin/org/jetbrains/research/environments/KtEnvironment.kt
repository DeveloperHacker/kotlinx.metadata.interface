package org.jetbrains.research.environments

import kotlinx.metadata.ClassName
import org.jetbrains.research.elements.KtClass
import org.jetbrains.research.elements.KtClassElement
import javax.lang.model.element.Element

interface KtEnvironment {
    fun getKtClassElement(javaElement: Element): KtClassElement?

    fun getKtClass(javaElement: Element): KtClass?

    fun getKtClassElement(name: ClassName): KtClassElement?

    fun getKtClass(name: ClassName): KtClass?

    fun findAllKtClassElements(): Sequence<KtClassElement>

    fun findAllKtClasses(): Sequence<KtClass>

    fun getRootClassElements(): List<KtClassElement>

    fun <T : Annotation> getClassElementsWithAnnotation(annotationType: Class<T>): List<KtClassElement>
}
