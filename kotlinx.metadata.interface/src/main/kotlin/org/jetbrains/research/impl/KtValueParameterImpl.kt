package org.jetbrains.research.impl

import kotlinx.metadata.Flags
import kotlinx.metadata.KmValueParameterVisitor
import org.jetbrains.research.elements.KtElement
import org.jetbrains.research.elements.KtEnvironment
import org.jetbrains.research.elements.KtType
import org.jetbrains.research.elements.KtValueParameter
import org.jetbrains.research.flags.KtParametersFlags
import org.jetbrains.research.utlis.KtWrapper

data class KtValueParameterImpl(
    override val flags: KtParametersFlags,
    override val name: String,
    override val type: KtType,
    override val isVararg: Boolean,
    override val getParent: () -> KtElement
) : KtValueParameter {
    companion object {
        operator fun invoke(
            environment: KtEnvironment,
            parent: () -> KtElement,
            flags: Flags,
            name: String,
            resultListener: (KtValueParameter) -> Unit
        ) =
            object : KmValueParameterVisitor() {
                lateinit var type: KtType
                lateinit var isVararg: KtWrapper<Boolean>
                lateinit var self: KtValueParameter
                val lazySelf = { self }

                override fun visitEnd() {
                    val valueParameterFlags = KtParametersFlags(flags)
                    self = KtValueParameterImpl(valueParameterFlags, name, type, isVararg.value, parent)
                    resultListener(self)
                }

                override fun visitType(flags: Flags) = KtTypeImpl(environment, lazySelf, flags) {
                    isVararg = KtWrapper(false)
                    type = it
                }

                override fun visitVarargElementType(flags: Flags) = KtTypeImpl(environment, lazySelf, flags) {
                    isVararg = KtWrapper(true)
                    type = it
                }
            }
    }
}
