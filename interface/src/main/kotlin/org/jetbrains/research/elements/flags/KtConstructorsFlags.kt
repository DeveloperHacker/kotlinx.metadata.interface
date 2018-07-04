package org.jetbrains.research.elements.flags

import kotlinx.metadata.Flags
import org.jetbrains.kotlin.metadata.deserialization.Flags.IS_SECONDARY
import java.util.*

class KtConstructorsFlags(flags: Flags) : KtCallablesFlags(flags) {
    val isSecondary: Boolean by lazy { IS_SECONDARY.get(flags) }
    val isPrimary: Boolean by lazy { !isSecondary }

    override val names by lazy {
        val result = ArrayList<String>()
        if (isSecondary) result.add("IS_SECONDARY")
        super.names + result
    }
}