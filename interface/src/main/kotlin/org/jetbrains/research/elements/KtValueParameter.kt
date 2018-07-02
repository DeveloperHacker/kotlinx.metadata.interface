package org.jetbrains.research.elements

import kotlinx.metadata.Flags
import kotlinx.metadata.KmValueParameterVisitor
import org.jetbrains.research.KtWrapper
import org.jetbrains.research.elements.flags.KtParametersFlags
import org.jetbrains.research.environments.KtEnvironment

data class KtValueParameter(val flags: KtParametersFlags, val name: String, val type: KtType, val isVararg: Boolean) {
    companion object {
        operator fun invoke(environment: KtEnvironment, flags: Flags, name: String, resultListener: (KtValueParameter) -> Unit) =
            object : KmValueParameterVisitor() {
                lateinit var type: KtType
                lateinit var isVararg: KtWrapper<Boolean>

                override fun visitEnd() {
                    val valueParameterFlags = KtParametersFlags(flags)
                    val valueParameter = KtValueParameter(valueParameterFlags, name, type, isVararg.value)
                    resultListener(valueParameter)
                }

                override fun visitType(flags: Flags) = KtType(environment, flags) {
                    isVararg = KtWrapper(false)
                    type = it
                }

                override fun visitVarargElementType(flags: Flags) = KtType(environment, flags) {
                    isVararg = KtWrapper(true)
                    type = it
                }
            }
    }
}