package org.jetbrains.research.elements

import kotlinx.metadata.*
import kotlinx.metadata.jvm.JvmFunctionExtensionVisitor
import org.jetbrains.research.elements.flags.KtFunctionsFlags
import org.jetbrains.research.environments.KtEnvironment

data class KtFunction(
    val flags: KtFunctionsFlags,
    val name: String,
    val contract: KtContract?,
    val extensions: List<KtExtension>,
    val receiverParameterType: KtType?,
    val returnType: KtType,
    val typeParameters: List<KtTypeParameter>,
    val valueParameters: List<KtValueParameter>,
    val versionRequirement: KtVersionRequirement?
) {
    companion object {
        operator fun invoke(environment: KtEnvironment, flags: Flags, name: String, resultListener: (KtFunction) -> Unit) =
            object : KmFunctionVisitor() {
                var contract: KtContract? = null
                val extensions = ArrayList<KtExtension>()
                var receiverParameterType: KtType? = null
                lateinit var returnType: KtType
                val typeParameters = ArrayList<KtTypeParameter>()
                val valueParameters = ArrayList<KtValueParameter>()
                var versionRequirement: KtVersionRequirement? = null

                override fun visitContract() = KtContract(environment) {
                    contract = it
                }

                override fun visitEnd() {
                    val functionFlags = KtFunctionsFlags(flags)
                    val function = KtFunction(
                        functionFlags,
                        name,
                        contract,
                        extensions,
                        receiverParameterType,
                        returnType,
                        typeParameters,
                        valueParameters,
                        versionRequirement
                    )
                    resultListener(function)
                }

                override fun visitExtensions(type: KmExtensionType) = KtExtension(type) {
                    extensions.add(it)
                }

                override fun visitReceiverParameterType(flags: Flags) = KtType(environment, flags) {
                    receiverParameterType = it
                }

                override fun visitReturnType(flags: Flags) = KtType(environment, flags) {
                    returnType = it
                }

                override fun visitTypeParameter(flags: Flags, name: String, id: Int, variance: KmVariance) =
                    KtTypeParameter(environment, flags, name, id, variance) {
                        typeParameters.add(it)
                    }

                override fun visitValueParameter(flags: Flags, name: String) = KtValueParameter(environment, flags, name) {
                    valueParameters.add(it)
                }

                override fun visitVersionRequirement() = KtVersionRequirement {
                    versionRequirement = it
                }
            }
    }

    class KtExtension {

        var descriptor: String? = null
            private set

        var lambdaClassOriginName: String? = null
            private set

        override fun toString() =
            "${javaClass.name}(descriptor=$descriptor,lambdaClassOriginName=$lambdaClassOriginName)"

        companion object {
            operator fun invoke(type: KmExtensionType, resultListener: (KtExtension) -> Unit): KmFunctionExtensionVisitor? {
                val extension = KtExtension()
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