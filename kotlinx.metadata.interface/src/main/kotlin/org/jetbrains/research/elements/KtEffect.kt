package org.jetbrains.research.elements

import kotlinx.metadata.KmEffectInvocationKind
import kotlinx.metadata.KmEffectType

interface KtEffect : KtElement {

    /**
     * ToDo kotlin comment
     **/
    override val getParent: () -> KtContract

    /**
     * ToDo kotlin comment
     **/
    val type: KmEffectType

    /**
     * ToDo kotlin comment
     **/
    val invocationKind: KmEffectInvocationKind?

    /**
     * ToDo kotlin comment
     **/
    val conclusionOfConditionalEffect: KtEffectExpression?

    /**
     * ToDo kotlin comment
     **/
    val constructorArguments: List<KtEffectExpression>
}
