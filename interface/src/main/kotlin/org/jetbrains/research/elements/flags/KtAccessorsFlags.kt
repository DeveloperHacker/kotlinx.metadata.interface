package org.jetbrains.research.elements.flags

import kotlinx.metadata.Flags
import org.jetbrains.kotlin.metadata.deserialization.Flags.*
import java.util.*

class KtAccessorsFlags(flags: Flags): KtFlags(flags) {
    val isNotDefault by lazy { IS_NOT_DEFAULT.get(flags) }
    val isExternalAccessor by lazy { IS_EXTERNAL_ACCESSOR.get(flags) }
    val isInlineAccessor by lazy { IS_INLINE_ACCESSOR.get(flags) }

    override val names by lazy {
        val result = ArrayList<String>()
        if (isNotDefault) result.add("IS_NOT_DEFAULT")
        if (isExternalAccessor) result.add("IS_EXTERNAL_ACCESSOR")
        if (isInlineAccessor) result.add("IS_INLINE_ACCESSOR")
        result
    }
}