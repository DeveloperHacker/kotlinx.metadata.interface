package org.jetbrains.research.elements

import kotlinx.metadata.KmContractVisitor
import kotlinx.metadata.KmEffectInvocationKind
import kotlinx.metadata.KmEffectType
import org.jetbrains.research.environments.KtEnvironment

data class KtContract(val effects: List<KtEffect>) {
    companion object {
        operator fun invoke(environment: KtEnvironment, resultListener: (KtContract) -> Unit) = object : KmContractVisitor() {
            val effects = ArrayList<KtEffect>()

            override fun visitEffect(type: KmEffectType, invocationKind: KmEffectInvocationKind?) =
                KtEffect(environment, type, invocationKind) {
                    effects.add(it)
                }

            override fun visitEnd() {
                val contract = KtContract(effects)
                resultListener(contract)
            }
        }
    }
}