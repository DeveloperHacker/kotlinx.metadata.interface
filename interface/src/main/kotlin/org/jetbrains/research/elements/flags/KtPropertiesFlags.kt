package org.jetbrains.research.elements.flags

import kotlinx.metadata.Flags
import org.jetbrains.kotlin.metadata.deserialization.Flags.*
import java.util.*

class KtPropertiesFlags(flags: Flags): KtCallablesFlags(flags) {
    val isVar by lazy { IS_VAR.get(flags) }
    val hasGetter by lazy { HAS_GETTER.get(flags) }
    val hasSetter by lazy { HAS_SETTER.get(flags) }
    val isConst by lazy { IS_CONST.get(flags) }
    val isLateinit by lazy { IS_LATEINIT.get(flags) }
    val hasConstant by lazy { HAS_CONSTANT.get(flags) }
    val isExternalProperty by lazy { IS_EXTERNAL_PROPERTY.get(flags) }
    val isDelegated by lazy { IS_DELEGATED.get(flags) }
    val isExpectProperty by lazy { IS_EXPECT_PROPERTY.get(flags) }

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