package org.jetbrains.research.impl

import kotlinx.metadata.KmContractVisitor
import kotlinx.metadata.KmEffectInvocationKind
import kotlinx.metadata.KmEffectType
import org.jetbrains.research.elements.KtContract
import org.jetbrains.research.elements.KtEffect
import org.jetbrains.research.elements.KtEnvironment
import org.jetbrains.research.elements.KtFunction

data class KtContractImpl(
    override val effects: List<KtEffect>,
    override val getParent: () -> KtFunction
) : KtContract {
    companion object {
        operator fun invoke(environment: KtEnvironment, parent: () -> KtFunction, resultListener: (KtContract) -> Unit) = object : KmContractVisitor() {
            val effects = ArrayList<KtEffect>()
            lateinit var self: KtContract
            val lazySelf = { self }

            override fun visitEffect(type: KmEffectType, invocationKind: KmEffectInvocationKind?) =
                KtEffectImpl(environment, lazySelf, type, invocationKind) {
                    effects.add(it)
                }

            override fun visitEnd() {
                self = KtContractImpl(effects, parent)
                resultListener(self)
            }
        }
    }
}
