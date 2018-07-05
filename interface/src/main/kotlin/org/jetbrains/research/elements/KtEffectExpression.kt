package org.jetbrains.research.elements

import org.jetbrains.research.flags.KtCallablesFlags

interface KtEffectExpression : KtElement, KtWithFlags<KtCallablesFlags> {

    /**
     * ToDo kotlin comment
     **/
    val parameterIndex: Int?

    /**
     * ToDo kotlin comment
     **/
    val andArguments: List<KtEffectExpression>

    /**
     * ToDo kotlin comment
     **/
    val orArguments: List<KtEffectExpression>

    /**
     * ToDo kotlin comment
     **/
    val isInstanceType: KtType?

    /**
     * ToDo kotlin comment
     **/
    val constantValue: KtConstantValue<Any?>?

    interface KtConstantValue<T> {

        /**
         * ToDo kotlin comment
         **/
        val value: T
    }
}
