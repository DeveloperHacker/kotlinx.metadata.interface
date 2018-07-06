package org.jetbrains.research.impl

import kotlinx.metadata.Flags
import kotlinx.metadata.KmEffectExpressionVisitor
import org.jetbrains.research.elements.KtEffectExpression
import org.jetbrains.research.elements.KtElement
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
    override val constantValue: KtConstantValue<Any?>?,
    override val getParent: () -> KtElement
) : KtEffectExpression {
    companion object {
        operator fun invoke(
            environment: KtEnvironment,
            parent: () -> KtElement,
            resultListener: (KtEffectExpression) -> Unit
        ): KmEffectExpressionVisitor =
            object : KmEffectExpressionVisitor() {
                lateinit var flags: KtCallablesFlags
                lateinit var parameterIndex: KtWrapper<Int?>
                val andArguments = ArrayList<KtEffectExpression>()
                val orArguments = ArrayList<KtEffectExpression>()
                var isInstanceType: KtType? = null
                var constantValue: KtConstantValue<Any?>? = null
                lateinit var self: KtEffectExpression
                val lazySelf = { self }

                override fun visit(flags: Flags, parameterIndex: Int?) {
                    this.flags = KtCallablesFlags(flags)
                    this.parameterIndex = KtWrapper(parameterIndex)
                }

                override fun visitAndArgument() = KtEffectExpressionImpl(environment, lazySelf) {
                    andArguments.add(it)
                }

                override fun visitConstantValue(value: Any?) {
                    constantValue = KtConstantValue(value)
                }

                override fun visitEnd() {
                    self = KtEffectExpressionImpl(
                        flags,
                        parameterIndex.value,
                        andArguments,
                        orArguments,
                        isInstanceType,
                        constantValue,
                        parent
                    )
                    resultListener(self)
                }

                override fun visitIsInstanceType(flags: Flags) = KtTypeImpl(environment, lazySelf, flags) {
                    isInstanceType = it
                }

                override fun visitOrArgument() = KtEffectExpressionImpl(environment, lazySelf) {
                    orArguments.add(it)
                }
            }
    }

    data class KtConstantValue<T>(override val value: T) : KtEffectExpression.KtConstantValue<T>
}
