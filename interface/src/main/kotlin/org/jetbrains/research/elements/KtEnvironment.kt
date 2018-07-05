package org.jetbrains.research.elements

import javax.lang.model.element.Element

interface KtEnvironment {

    /**
     * ToDo kotlin comment
     **/
    fun getKtClassElement(javaElement: Element): KtClassElement<*>?

    /**
     * ToDo kotlin comment
     **/
    fun findAllKtClassElements(): Sequence<KtClassElement<*>>

    /**
     * ToDo kotlin comment
     **/
    fun getRootClassElements(): Sequence<KtClassElement<*>>

    /**
     * ToDo kotlin comment
     **/
    fun <T : Annotation> getClassElementsWithAnnotation(annotationType: Class<T>): Sequence<KtClassElement<*>>
}
