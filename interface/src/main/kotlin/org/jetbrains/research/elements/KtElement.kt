package org.jetbrains.research.elements

import javax.lang.model.element.Element

interface KtElement {

    /**
     * ToDo kotlin comment
     **/
    val javaElement: Element?
        get() = null

    /**
     * ToDo kotlin comment
     **/
    val getParent: () -> KtElement?
}
