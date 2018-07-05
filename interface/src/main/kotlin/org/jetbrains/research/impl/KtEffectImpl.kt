package org.jetbrains.research.impl

import kotlinx.metadata.KmEffectInvocationKind
import kotlinx.metadata.KmEffectType
import kotlinx.metadata.KmEffectVisitor
import org.jetbrains.research.elements.KtEffect
import org.jetbrains.research.elements.KtEffectExpression
import org.jetbrains.research.elements.KtEnvironment

data class KtEffectImpl(
    override val type: KmEffectType,
    override val invocationKind: KmEffectInvocationKind?,
    override val conclusionOfConditionalEffect: KtEffectExpression?,
    override val constructorArguments: List<KtEffectExpression>
) : KtEffect {
    companion object {
        operator fun invoke(
            environment: KtEnvironment,
            type: KmEffectType,
            invocationKind: KmEffectInvocationKind?,
            resultListener: (KtEffect) -> Unit
        ) = object : KmEffectVisitor() {
            var conclusionOfConditionalEffect: KtEffectExpression? = null
            val constructorArguments = ArrayList<KtEffectExpression>()

            override fun visitConclusionOfConditionalEffect() = KtEffectExpressionImpl(environment) {
                conclusionOfConditionalEffect = it
            }

            override fun visitConstructorArgument() = KtEffectExpressionImpl(environment) {
                constructorArguments.add(it)
            }

            override fun visitEnd() {
                val effect = KtEffectImpl(type, invocationKind, conclusionOfConditionalEffect, constructorArguments)
                resultListener(effect)
            }
        }
    }
}
