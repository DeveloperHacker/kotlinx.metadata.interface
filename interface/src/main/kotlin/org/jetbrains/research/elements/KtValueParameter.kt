package org.jetbrains.research.elements

import org.jetbrains.research.flags.KtParametersFlags

interface KtValueParameter : KtElement, KtWithFlags<KtParametersFlags> {

    /**
     * ToDo kotlin comment
     **/
    val name: String

    /**
     * ToDo kotlin comment
     **/
    val type: KtType

    /**
     * ToDo kotlin comment
     **/
    val isVararg: Boolean
}
