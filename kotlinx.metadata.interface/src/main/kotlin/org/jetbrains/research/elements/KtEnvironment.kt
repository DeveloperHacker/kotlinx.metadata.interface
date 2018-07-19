package org.jetbrains.research.elements

import javax.lang.model.element.Element

interface KtEnvironment {

    /**
     * ToDo kotlin comment
     **/
    fun getRootKtElements(): Sequence<KtElement>

    /**
     * ToDo kotlin comment
     **/
    fun getKtElement(javaElement: Element): KtElement?

    /**
     * ToDo kotlin comment
     **/
    fun <T : Annotation> getKtElements(annotationType: Class<T>): Sequence<KtElement>

    /**
     * ToDo kotlin comment
     **/
    fun getAllKtElements(): Sequence<KtElement>

    /**
     * ToDo kotlin comment
     **/
    fun cache(ktElement: KtElement): Boolean
}
