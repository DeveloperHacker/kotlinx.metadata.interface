package org.jetbrains.research.impl

import kotlinx.metadata.KmEffectInvocationKind
import kotlinx.metadata.KmEffectType
import kotlinx.metadata.KmEffectVisitor
import org.jetbrains.research.elements.KtContract
import org.jetbrains.research.elements.KtEffect
import org.jetbrains.research.elements.KtEffectExpression
import org.jetbrains.research.elements.KtEnvironment

data class KtEffectImpl(
    override val type: KmEffectType,
    override val invocationKind: KmEffectInvocationKind?,
    override val conclusionOfConditionalEffect: KtEffectExpression?,
    override val constructorArguments: List<KtEffectExpression>,
    override val getParent: () -> KtContract
) : KtEffect {
    companion object {
        operator fun invoke(
            environment: KtEnvironment,
            parent: () -> KtContract,
            type: KmEffectType,
            invocationKind: KmEffectInvocationKind?,
            resultListener: (KtEffect) -> Unit
        ) = object : KmEffectVisitor() {
            var conclusionOfConditionalEffect: KtEffectExpression? = null
            val constructorArguments = ArrayList<KtEffectExpression>()
            lateinit var self: KtEffect
            val lazySelf = { self }

            override fun visitConclusionOfConditionalEffect() = KtEffectExpressionImpl(environment, lazySelf) {
                conclusionOfConditionalEffect = it
            }

            override fun visitConstructorArgument() = KtEffectExpressionImpl(environment, lazySelf) {
                constructorArguments.add(it)
            }

            override fun visitEnd() {
                self = KtEffectImpl(type, invocationKind, conclusionOfConditionalEffect, constructorArguments, parent)
                resultListener(self)
            }
        }
    }
}
