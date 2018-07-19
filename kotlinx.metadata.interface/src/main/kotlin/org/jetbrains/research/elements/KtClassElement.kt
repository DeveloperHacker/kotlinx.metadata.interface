package org.jetbrains.research.elements

import kotlinx.metadata.jvm.KotlinClassMetadata
import javax.lang.model.element.Element

interface KtClassElement<T : KotlinClassMetadata> : KtElement {

    /**
     * ToDo kotlin comment
     **/
    override val javaElement: Element

    /**
     * ToDo kotlin comment
     **/
    val metadata: T
}
