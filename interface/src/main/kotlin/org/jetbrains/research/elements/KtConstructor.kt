package org.jetbrains.research.elements

import org.jetbrains.research.flags.KtConstructorsFlags
import javax.lang.model.element.Element

interface KtConstructor : KtElement, KtWithFlags<KtConstructorsFlags> {

    /**
     * ToDo kotlin comment
     **/
    override val getParent: () -> KtClass

    /**
     * ToDo kotlin comment
     **/
    override val javaElement: Element?

    /**
     * ToDo kotlin comment
     **/
    val extensions: List<KtExtension>

    /**
     * ToDo kotlin comment
     **/
    val valueParameters: List<KtValueParameter>

    /**
     * ToDo kotlin comment
     **/
    val versionRequirement: KtVersionRequirement?

    interface KtExtension {

        /**
         * ToDo kotlin comment
         **/
        val descriptor: String?
    }
}
