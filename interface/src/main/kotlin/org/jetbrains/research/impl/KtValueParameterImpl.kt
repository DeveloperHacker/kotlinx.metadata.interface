package org.jetbrains.research.impl

import kotlinx.metadata.Flags
import kotlinx.metadata.KmValueParameterVisitor
import org.jetbrains.research.elements.KtEnvironment
import org.jetbrains.research.elements.KtType
import org.jetbrains.research.elements.KtValueParameter
import org.jetbrains.research.flags.KtParametersFlags
import org.jetbrains.research.utlis.KtWrapper

data class KtValueParameterImpl(
    override val flags: KtParametersFlags,
    override val name: String,
    override val type: KtType,
    override val isVararg: Boolean
) : KtValueParameter {
    companion object {
        operator fun invoke(environment: KtEnvironment, flags: Flags, name: String, resultListener: (KtValueParameter) -> Unit) =
            object : KmValueParameterVisitor() {
                lateinit var type: KtType
                lateinit var isVararg: KtWrapper<Boolean>

                override fun visitEnd() {
                    val valueParameterFlags = KtParametersFlags(flags)
                    val valueParameter = KtValueParameterImpl(valueParameterFlags, name, type, isVararg.value)
                    resultListener(valueParameter)
                }

                override fun visitType(flags: Flags) = KtTypeImpl(environment, flags) {
                    isVararg = KtWrapper(false)
                    type = it
                }

                override fun visitVarargElementType(flags: Flags) = KtTypeImpl(environment, flags) {
                    isVararg = KtWrapper(true)
                    type = it
                }
            }
    }
}
