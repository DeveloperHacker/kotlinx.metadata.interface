package org.jetbrains.research.elements.flags

import kotlinx.metadata.Flags
import org.jetbrains.kotlin.metadata.deserialization.Flags.*
import java.util.*

class KtFunctionsFlags(flags: Flags): KtCallablesFlags(flags) {
    val isOperator by lazy { IS_OPERATOR.get(flags) }
    val isInfix by lazy { IS_INFIX.get(flags) }
    val isInline by lazy { IS_INLINE.get(flags) }
    val isTailrec by lazy { IS_TAILREC.get(flags) }
    val isExternalFunction by lazy { IS_EXTERNAL_FUNCTION.get(flags) }
    val isSuspend by lazy { IS_SUSPEND.get(flags) }
    val isExpectFunction by lazy { IS_EXPECT_FUNCTION.get(flags) }

    override val names by lazy {
        val result = ArrayList<String>()
        if (isOperator) result.add("IS_OPERATOR")
        if (isInfix) result.add("IS_INFIX")
        if (isInline) result.add("IS_INLINE")
        if (isTailrec) result.add("IS_TAILREC")
        if (isExternalFunction) result.add("IS_EXTERNAL_FUNCTION")
        if (isSuspend) result.add("IS_SUSPEND")
        if (isExpectFunction) result.add("IS_EXPECT_FUNCTION")
        super.names + result
    }
}