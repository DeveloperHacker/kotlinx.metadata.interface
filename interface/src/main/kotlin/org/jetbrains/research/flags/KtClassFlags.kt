package org.jetbrains.research.flags

import kotlinx.metadata.Flags
import org.jetbrains.kotlin.metadata.deserialization.Flags.*
import java.util.*

class KtClassFlags(flags: Flags) : KtDeclarationFlags(flags) {
    val classKind by lazy { CLASS_KIND.get(flags)?.let { KtClassKind(it) } }
    val isInner: Boolean by lazy { IS_INNER.get(flags) }
    val isData: Boolean by lazy { IS_DATA.get(flags) }
    val isExternalClass: Boolean by lazy { IS_EXTERNAL_CLASS.get(flags) }
    val isExpectClass: Boolean by lazy { IS_EXPECT_CLASS.get(flags) }
    val isInlineClass: Boolean by lazy { IS_INLINE_CLASS.get(flags) }

    override val names by lazy {
        val result = ArrayList<String>()
        classKind?.let { result.add(it.toString()) }
        if (isInner) result.add("IS_INNER")
        if (isData) result.add("IS_DATA")
        if (isExternalClass) result.add("IS_EXTERNAL_CLASS")
        if (isExpectClass) result.add("IS_EXPECT_CLASS")
        if (isInlineClass) result.add("IS_INLINE_CLASS")
        super.names + result
    }
}