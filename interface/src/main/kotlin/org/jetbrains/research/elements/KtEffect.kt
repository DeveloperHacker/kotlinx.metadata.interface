package org.jetbrains.research.elements

import kotlinx.metadata.KmEffectInvocationKind
import kotlinx.metadata.KmEffectType
import kotlinx.metadata.KmEffectVisitor
import org.jetbrains.research.environments.KtEnvironment

data class KtEffect(
    val type: KmEffectType,
    val invocationKind: KmEffectInvocationKind?,
    val conclusionOfConditionalEffect: KtEffectExpression?,
    val constructorArguments: List<KtEffectExpression>
) {
    companion object {
        operator fun invoke(
            environment: KtEnvironment,
            type: KmEffectType,
            invocationKind: KmEffectInvocationKind?,
            resultListener: (KtEffect) -> Unit
        ) = object : KmEffectVisitor() {
            var conclusionOfConditionalEffect: KtEffectExpression? = null
            val constructorArguments = ArrayList<KtEffectExpression>()

            override fun visitConclusionOfConditionalEffect() = KtEffectExpression(environment) {
                conclusionOfConditionalEffect = it
            }

            override fun visitConstructorArgument() = KtEffectExpression(environment) {
                constructorArguments.add(it)
            }

            override fun visitEnd() {
                val effect = KtEffect(type, invocationKind, conclusionOfConditionalEffect, constructorArguments)
                resultListener(effect)
            }
        }
    }
}