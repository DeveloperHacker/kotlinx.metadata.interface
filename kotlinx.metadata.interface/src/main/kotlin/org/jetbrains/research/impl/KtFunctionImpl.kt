package org.jetbrains.research.impl

import kotlinx.metadata.*
import kotlinx.metadata.jvm.JvmFunctionExtensionVisitor
import org.jetbrains.research.elements.*
import org.jetbrains.research.flags.KtFunctionsFlags
import javax.lang.model.element.Element

data class KtFunctionImpl(
    override val flags: KtFunctionsFlags,
    override val name: String,
    override val contract: KtContract?,
    override val extensions: List<KtFunction.KtExtension>,
    override val receiverParameterType: KtType?,
    override val returnType: KtType,
    override val typeParameters: List<KtTypeParameter>,
    override val valueParameters: List<KtValueParameter>,
    override val versionRequirement: KtVersionRequirement?,
    override val getParent: () -> KtElement
) : KtFunction {

    override var javaElement: Element? = null
        internal set

    companion object {
        operator fun invoke(
            environment: KtEnvironment,
            parent: () -> KtElement,
            flags: Flags,
            name: String,
            resultListener: (KtFunctionImpl) -> Unit
        ) = object : KmFunctionVisitor() {
            var contract: KtContract? = null
            val extensions = ArrayList<KtFunction.KtExtension>()
            var receiverParameterType: KtType? = null
            lateinit var returnType: KtType
            val typeParameters = ArrayList<KtTypeParameter>()
            val valueParameters = ArrayList<KtValueParameter>()
            var versionRequirement: KtVersionRequirement? = null
            lateinit var self: KtFunctionImpl
            val lazySelf = { self }

            override fun visitContract() = KtContractImpl(environment, lazySelf) {
                contract = it
            }

            override fun visitEnd() {
                val functionFlags = KtFunctionsFlags(flags)
                self = KtFunctionImpl(
                    functionFlags,
                    name,
                    contract,
                    extensions,
                    receiverParameterType,
                    returnType,
                    typeParameters,
                    valueParameters,
                    versionRequirement,
                    parent
                )
                resultListener(self)
            }

            override fun visitExtensions(type: KmExtensionType) = KtExtensionImpl(type) {
                extensions.add(it)
            }

            override fun visitReceiverParameterType(flags: Flags) = KtTypeImpl(environment, lazySelf, flags) {
                receiverParameterType = it
            }

            override fun visitReturnType(flags: Flags) = KtTypeImpl(environment, lazySelf, flags) {
                returnType = it
            }

            override fun visitTypeParameter(flags: Flags, name: String, id: Int, variance: KmVariance) =
                KtTypeParameterImpl(environment, lazySelf, flags, name, id, variance) {
                    typeParameters.add(it)
                }

            override fun visitValueParameter(flags: Flags, name: String) = KtValueParameterImpl(environment, lazySelf, flags, name) {
                valueParameters.add(it)
            }

            override fun visitVersionRequirement() = KtVersionRequirementImpl {
                versionRequirement = it
            }
        }
    }

    class KtExtensionImpl : KtFunction.KtExtension {

        override var descriptor: String? = null
            private set

        override var lambdaClassOriginName: String? = null
            private set

        override fun toString() =
            "${javaClass.name}(descriptor=$descriptor,lambdaClassOriginName=$lambdaClassOriginName)"

        companion object {
            operator fun invoke(type: KmExtensionType, resultListener: (KtFunction.KtExtension) -> Unit): KmFunctionExtensionVisitor? {
                val extension = KtExtensionImpl()
                resultListener(extension)
                if (type != JvmFunctionExtensionVisitor.TYPE) return null
                return object : JvmFunctionExtensionVisitor() {
                    override fun visit(desc: String?) {
                        extension.descriptor = desc
                    }

                    override fun visitLambdaClassOriginName(internalName: String) {
                        extension.lambdaClassOriginName = internalName
                    }
                }
            }
        }
    }
}
