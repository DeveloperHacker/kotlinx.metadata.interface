package org.jetbrains.research.impl

import kotlinx.metadata.KmContractVisitor
import kotlinx.metadata.KmEffectInvocationKind
import kotlinx.metadata.KmEffectType
import org.jetbrains.research.elements.KtContract
import org.jetbrains.research.elements.KtEffect
import org.jetbrains.research.elements.KtEnvironment

data class KtContractImpl(override val effects: List<KtEffect>) : KtContract {
    companion object {
        operator fun invoke(environment: KtEnvironment, resultListener: (KtContract) -> Unit) = object : KmContractVisitor() {
            val effects = ArrayList<KtEffect>()

            override fun visitEffect(type: KmEffectType, invocationKind: KmEffectInvocationKind?) =
                KtEffectImpl(environment, type, invocationKind) {
                    effects.add(it)
                }

            override fun visitEnd() {
                val contract = KtContractImpl(effects)
                resultListener(contract)
            }
        }
    }
}
