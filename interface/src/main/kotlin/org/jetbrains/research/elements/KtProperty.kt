package org.jetbrains.research.elements

import kotlinx.metadata.*
import kotlinx.metadata.jvm.JvmPropertyExtensionVisitor
import org.jetbrains.research.KtWrapper
import org.jetbrains.research.elements.flags.KtFunctionsFlags
import org.jetbrains.research.elements.flags.KtPropertiesFlags
import org.jetbrains.research.environments.KtEnvironment

data class KtProperty(
    val flags: KtPropertiesFlags,
    val name: String,
    val getterFlags: KtFunctionsFlags,
    val setterFlags: KtFunctionsFlags,
    val extensions: List<KtExtensions>,
    val receiverParameterType: KtType?,
    val returnType: KtType,
    val setterValueParameter: KtValueParameter?,
    val typeParameters: List<KtTypeParameter>,
    val versionRequirement: KtVersionRequirement?
) {
    companion object {
        operator fun invoke(
            environment: KtEnvironment,
            flags: Flags,
            name: String,
            getterFlags: Flags,
            setterFlags: Flags,
            resultListener: (KtProperty) -> Unit
        ) =
            object : KmPropertyVisitor() {
                val extensions = ArrayList<KtExtensions>()
                var receiverParameterType: KtType? = null
                lateinit var returnType: KtType
                var setterValueParameter: KtValueParameter? = null
                val typeParameters = ArrayList<KtTypeParameter>()
                var versionRequirement: KtVersionRequirement? = null

                override fun visitEnd() {
                    val propertyFlags = KtPropertiesFlags(flags)
                    val setterFunctionFlags = KtFunctionsFlags(setterFlags)
                    val getterFunctionFlags = KtFunctionsFlags(getterFlags)
                    val property = KtProperty(
                        propertyFlags,
                        name,
                        setterFunctionFlags,
                        getterFunctionFlags,
                        extensions,
                        receiverParameterType,
                        returnType,
                        setterValueParameter,
                        typeParameters,
                        versionRequirement
                    )
                    resultListener(property)
                }

                override fun visitExtensions(type: KmExtensionType) = KtExtensions(type) {
                    extensions.add(it)
                }

                override fun visitReceiverParameterType(flags: Flags) = KtType(environment, flags) {
                    receiverParameterType = it
                }

                override fun visitReturnType(flags: Flags) = KtType(environment, flags) {
                    returnType = it
                }

                override fun visitSetterParameter(flags: Flags, name: String) = KtValueParameter(environment, flags, name) {
                    setterValueParameter = it
                }

                override fun visitTypeParameter(flags: Flags, name: String, id: Int, variance: KmVariance) =
                    KtTypeParameter(environment, flags, name, id, variance) {
                        typeParameters
                    }

                override fun visitVersionRequirement() = KtVersionRequirement {
                    versionRequirement = it
                }
            }
    }

    data class KtExtensions(
        val fieldName: String?,
        val fieldTypeDesc: String?,
        val getterDesc: String?,
        val setterDesc: String?,
        val syntheticMethodForAnnotations: String?
    ) {
        companion object {
            operator fun invoke(type: KmExtensionType, resultListener: (KtExtensions) -> Unit): KmPropertyExtensionVisitor? {
                if (type != JvmPropertyExtensionVisitor.TYPE) {
                    val extension = KtExtensions(null, null, null, null, null)
                    resultListener(extension)
                    return null
                }
                return object : JvmPropertyExtensionVisitor() {
                    lateinit var fieldName: KtWrapper<String?>
                    lateinit var fieldTypeDesc: KtWrapper<String?>
                    lateinit var getterDesc: KtWrapper<String?>
                    lateinit var setterDesc: KtWrapper<String?>
                    lateinit var syntheticMethodForAnnotations: KtWrapper<String?>

                    override fun visit(fieldName: String?, fieldTypeDesc: String?, getterDesc: String?, setterDesc: String?) {
                        this.fieldName = KtWrapper(fieldName)
                        this.fieldTypeDesc = KtWrapper(fieldTypeDesc)
                        this.getterDesc = KtWrapper(getterDesc)
                        this.setterDesc = KtWrapper(setterDesc)
                    }

                    override fun visitEnd() {
                        val extension = KtExtensions(
                            fieldName.value,
                            fieldTypeDesc.value,
                            getterDesc.value,
                            setterDesc.value,
                            syntheticMethodForAnnotations.value
                        )
                        resultListener(extension)
                    }

                    override fun visitSyntheticMethodForAnnotations(desc: String?) {
                        syntheticMethodForAnnotations = KtWrapper(desc)
                    }
                }
            }
        }
    }
}