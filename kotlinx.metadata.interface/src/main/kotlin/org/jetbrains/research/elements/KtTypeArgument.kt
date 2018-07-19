package org.jetbrains.research.elements

import kotlinx.metadata.KmVariance

sealed class KtTypeArgument : KtElement {

    /**
     * ToDo kotlin comment
     **/
    abstract override val getParent: () -> KtType

    /**
     * ToDo kotlin comment
     **/
    abstract class StarProjection : KtTypeArgument()

    /**
     * ToDo kotlin comment
     **/
    abstract class Simple : KtTypeArgument() {

        /**
         * ToDo kotlin comment
         **/
        abstract val variance: KmVariance

        /**
         * ToDo kotlin comment
         **/
        abstract val type: KtType
    }
}
