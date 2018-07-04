package org.jetbrains.research.elements.flags

import kotlinx.metadata.Flags
import org.jetbrains.kotlin.metadata.deserialization.Flags.IS_NEGATED
import org.jetbrains.kotlin.metadata.deserialization.Flags.IS_NULL_CHECK_PREDICATE
import java.util.*

class KtContractsFlags(flags: Flags) : KtFlags(flags) {
    val isNegated: Boolean by lazy { IS_NEGATED.get(flags) }
    val isNullCheckPredicate: Boolean by lazy { IS_NULL_CHECK_PREDICATE.get(flags) }

    override val names by lazy {
        val result = ArrayList<String>()
        if (isNegated) result.add("IS_NEGATED")
        if (isNullCheckPredicate) result.add("IS_NULL_CHECK_PREDICATE")
        result
    }
}