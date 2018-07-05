package org.jetbrains.research.elements

import kotlinx.metadata.KmVariance

interface KtTypeArgument : KtElement {

    /**
     * ToDo kotlin comment
     **/
    interface StarProjection : KtTypeArgument

    /**
     * ToDo kotlin comment
     **/
    interface Simple : KtTypeArgument {

        /**
         * ToDo kotlin comment
         **/
        val variance: KmVariance

        /**
         * ToDo kotlin comment
         **/
        val type: KtType
    }
}
