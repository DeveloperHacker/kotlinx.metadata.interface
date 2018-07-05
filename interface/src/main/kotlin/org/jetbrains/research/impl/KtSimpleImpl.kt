package org.jetbrains.research.impl

import kotlinx.metadata.Flags
import kotlinx.metadata.KmVariance
import org.jetbrains.research.elements.KtEnvironment
import org.jetbrains.research.elements.KtType
import org.jetbrains.research.elements.KtTypeArgument

data class KtSimpleImpl(override val variance: KmVariance, override val type: KtType) :
    KtTypeArgument.Simple {
    companion object {
        operator fun invoke(
            environment: KtEnvironment,
            flags: Flags,
            variance: KmVariance,
            resultListener: (KtTypeArgument.Simple) -> Unit
        ) = KtTypeImpl(environment, flags) {
            val typeArgument = KtSimpleImpl(variance, it)
            resultListener(typeArgument)
        }
    }
}