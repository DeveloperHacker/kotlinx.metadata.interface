package org.jetbrains.research.elements

import kotlinx.metadata.Flags
import kotlinx.metadata.KmVariance

sealed class KtTypeArgument {

    class StarProjection : KtTypeArgument()

    data class Simple(val variance: KmVariance, val type: KtType) : KtTypeArgument() {
        companion object {
            operator fun invoke(flags: Flags, variance: KmVariance, resultListener: (Simple) -> Unit) = KtType(flags) {
                val typeArgument = Simple(variance, it)
                resultListener(typeArgument)
            }
        }
    }
}