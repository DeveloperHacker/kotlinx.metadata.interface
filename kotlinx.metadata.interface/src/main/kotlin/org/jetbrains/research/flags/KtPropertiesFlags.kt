package org.jetbrains.research.flags

import kotlinx.metadata.Flags
import org.jetbrains.kotlin.metadata.deserialization.Flags.*
import java.util.*

class KtPropertiesFlags(flags: Flags) : KtCallablesFlags(flags) {
    val isVar: Boolean by lazy { IS_VAR.get(flags) }
    val hasGetter: Boolean by lazy { HAS_GETTER.get(flags) }
    val hasSetter: Boolean by lazy { HAS_SETTER.get(flags) }
    val isConst: Boolean by lazy { IS_CONST.get(flags) }
    val isLateinit: Boolean by lazy { IS_LATEINIT.get(flags) }
    val hasConstant: Boolean by lazy { HAS_CONSTANT.get(flags) }
    val isExternalProperty: Boolean by lazy { IS_EXTERNAL_PROPERTY.get(flags) }
    val isDelegated: Boolean by lazy { IS_DELEGATED.get(flags) }
    val isExpectProperty: Boolean by lazy { IS_EXPECT_PROPERTY.get(flags) }

    override val names by lazy {
        val result = ArrayList<String>()
        if (isVar) result.add("IS_VAR")
        if (hasGetter) result.add("HAS_GETTER")
        if (hasSetter) result.add("HAS_SETTER")
        if (isConst) result.add("IS_CONST")
        if (isLateinit) result.add("IS_LATEINIT")
        if (hasConstant) result.add("HAS_CONSTANT")
        if (isExternalProperty) result.add("IS_EXTERNAL_PROPERTY")
        if (isDelegated) result.add("IS_DELEGATED")
        if (isExpectProperty) result.add("IS_EXPECT_PROPERTY")
        super.names + result
    }
}