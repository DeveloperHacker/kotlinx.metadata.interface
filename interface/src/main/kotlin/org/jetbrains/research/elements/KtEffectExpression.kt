package org.jetbrains.research.elements

import kotlinx.metadata.Flags
import kotlinx.metadata.KmEffectExpressionVisitor
import org.jetbrains.research.KtWrapper
import org.jetbrains.research.elements.flags.KtCallablesFlags
import org.jetbrains.research.environments.KtEnvironment

data class KtEffectExpression(
    val flags: KtCallablesFlags,
    val parameterIndex: Int?,
    val andArguments: List<KtEffectExpression>,
    val orArguments: List<KtEffectExpression>,
    val isInstanceType: KtType?,
    val constantValue: KtConstantValue<Any?>?
) {
    companion object {
        operator fun invoke(environment: KtEnvironment, resultListener: (KtEffectExpression) -> Unit): KmEffectExpressionVisitor =
            object : KmEffectExpressionVisitor() {
                lateinit var flags: KtCallablesFlags
                lateinit var parameterIndex: KtWrapper<Int?>
                val andArguments = ArrayList<KtEffectExpression>()
                val orArguments = ArrayList<KtEffectExpression>()
                var isInstanceType: KtType? = null
                var constantValue: KtConstantValue<Any?>? = null

                override fun visit(flags: Flags, parameterIndex: Int?) {
                    this.flags = KtCallablesFlags(flags)
                    this.parameterIndex = KtWrapper(parameterIndex)
                }

                override fun visitAndArgument() = KtEffectExpression(environment) {
                    andArguments.add(it)
                }

                override fun visitConstantValue(value: Any?) {
                    constantValue = KtConstantValue(value)
                }

                override fun visitEnd() {
                    val effectExpression =
                        KtEffectExpression(flags, parameterIndex.value, andArguments, orArguments, isInstanceType, constantValue)
                    resultListener(effectExpression)
                }

                override fun visitIsInstanceType(flags: Flags) = KtType(environment, flags) {
                    isInstanceType = it
                }

                override fun visitOrArgument() = KtEffectExpression(environment) {
                    orArguments.add(it)
                }
            }
    }

    data class KtConstantValue<T>(val value: T)
}