package org.jetbrains.research.impl

import kotlinx.metadata.Flags
import kotlinx.metadata.KmEffectExpressionVisitor
import org.jetbrains.research.elements.KtEffectExpression
import org.jetbrains.research.elements.KtEnvironment
import org.jetbrains.research.elements.KtType
import org.jetbrains.research.flags.KtCallablesFlags
import org.jetbrains.research.utlis.KtWrapper

data class KtEffectExpressionImpl(
    override val flags: KtCallablesFlags,
    override val parameterIndex: Int?,
    override val andArguments: List<KtEffectExpression>,
    override val orArguments: List<KtEffectExpression>,
    override val isInstanceType: KtType?,
    override val constantValue: KtConstantValue<Any?>?
) : KtEffectExpression {
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

                override fun visitAndArgument() = KtEffectExpressionImpl(environment) {
                    andArguments.add(it)
                }

                override fun visitConstantValue(value: Any?) {
                    constantValue = KtConstantValue(value)
                }

                override fun visitEnd() {
                    val effectExpression =
                        KtEffectExpressionImpl(flags, parameterIndex.value, andArguments, orArguments, isInstanceType, constantValue)
                    resultListener(effectExpression)
                }

                override fun visitIsInstanceType(flags: Flags) = KtTypeImpl(environment, flags) {
                    isInstanceType = it
                }

                override fun visitOrArgument() = KtEffectExpressionImpl(environment) {
                    orArguments.add(it)
                }
            }
    }

    data class KtConstantValue<T>(override val value: T) : KtEffectExpression.KtConstantValue<T>
}
