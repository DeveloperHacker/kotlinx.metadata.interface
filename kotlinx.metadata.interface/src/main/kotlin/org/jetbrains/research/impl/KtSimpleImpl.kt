package org.jetbrains.research.impl

import kotlinx.metadata.Flags
import kotlinx.metadata.KmTypeVisitor
import kotlinx.metadata.KmVariance
import org.jetbrains.research.elements.KtEnvironment
import org.jetbrains.research.elements.KtType
import org.jetbrains.research.elements.KtTypeArgument

data class KtSimpleImpl(
    override val variance: KmVariance,
    override val type: KtType,
    override val getParent: () -> KtType
) : KtTypeArgument.Simple() {
    companion object {
        operator fun invoke(
            environment: KtEnvironment,
            parent: () -> KtType,
            flags: Flags,
            variance: KmVariance,
            resultListener: (KtTypeArgument.Simple) -> Unit
        ): KmTypeVisitor {
            lateinit var self: KtTypeArgument.Simple
            return KtTypeImpl(environment, { self }, flags) {
                self = KtSimpleImpl(variance, it, parent)
                resultListener(self)
            }
        }
    }
}