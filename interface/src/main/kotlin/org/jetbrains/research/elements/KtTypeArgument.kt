package org.jetbrains.research.elements

import kotlinx.metadata.Flags
import kotlinx.metadata.KmVariance
import org.jetbrains.research.environments.KtEnvironment

sealed class KtTypeArgument {

    class StarProjection : KtTypeArgument()

    data class Simple(val variance: KmVariance, val type: KtType) : KtTypeArgument() {
        companion object {
            operator fun invoke(environment: KtEnvironment, flags: Flags, variance: KmVariance, resultListener: (Simple) -> Unit) =
                KtType(environment, flags) {
                    val typeArgument = Simple(variance, it)
                    resultListener(typeArgument)
                }
        }
    }
}