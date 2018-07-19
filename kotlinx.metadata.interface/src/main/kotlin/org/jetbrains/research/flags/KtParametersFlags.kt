package org.jetbrains.research.flags

import kotlinx.metadata.Flags
import org.jetbrains.kotlin.metadata.deserialization.Flags.*
import java.util.*

class KtParametersFlags(flags: Flags) : KtDeclarationFlags(flags) {
    val declaresDefaultValue: Boolean by lazy { DECLARES_DEFAULT_VALUE.get(flags) }
    val isCrossinline: Boolean by lazy { IS_CROSSINLINE.get(flags) }
    val isNoinline: Boolean by lazy { IS_NOINLINE.get(flags) }

    override val names by lazy {
        val result = ArrayList<String>()
        if (declaresDefaultValue) result.add("DECLARES_DEFAULT_VALUE")
        if (isCrossinline) result.add("IS_CROSSINLINE")
        if (isNoinline) result.add("IS_NOINLINE")
        super.names + result
    }
}