package org.jetbrains.research.elements

import kotlinx.metadata.KmAnnotation
import kotlinx.metadata.KmVariance
import org.jetbrains.research.flags.KtDeclarationFlags

interface KtTypeParameter : KtElement, KtWithFlags<KtDeclarationFlags> {

    /**
     * ToDo kotlin comment
     **/
    override val getParent: () -> KtElement

    /**
     * ToDo kotlin comment
     **/
    val name: String

    /**
     * ToDo kotlin comment
     **/
    val id: Int

    /**
     * ToDo kotlin comment
     **/
    val variance: KmVariance

    /**
     * ToDo kotlin comment
     **/
    val extensions: List<KtExtension>

    /**
     * ToDo kotlin comment
     **/
    val upperBounds: List<KtType>

    interface KtExtension {

        /**
         * ToDo kotlin comment
         **/
        val annotations: List<KmAnnotation>
    }
}
