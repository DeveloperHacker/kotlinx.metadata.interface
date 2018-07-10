package org.jetbrains.research.impl

import kotlinx.metadata.*
import kotlinx.metadata.jvm.JvmPropertyExtensionVisitor
import org.jetbrains.research.elements.*
import org.jetbrains.research.flags.KtFunctionsFlags
import org.jetbrains.research.flags.KtPropertiesFlags
import org.jetbrains.research.utlis.KtWrapper

data class KtPropertyImpl(
    override val flags: KtPropertiesFlags,
    override val name: String,
    override val getterFlags: KtFunctionsFlags,
    override val setterFlags: KtFunctionsFlags,
    override val extensions: List<KtProperty.KtExtension>,
    override val receiverParameterType: KtType?,
    override val returnType: KtType,
    override val setterValueParameter: KtValueParameter?,
    override val typeParameters: List<KtTypeParameter>,
    override val versionRequirement: KtVersionRequirement?,
    override val getParent: () -> KtElement
) : KtProperty {
    companion object {
        operator fun invoke(
            environment: KtEnvironment,
            parent: () -> KtElement,
            flags: Flags,
            name: String,
            getterFlags: Flags,
            setterFlags: Flags,
            resultListener: (KtProperty) -> Unit
        ) = object : KmPropertyVisitor() {
            val extensions = ArrayList<KtProperty.KtExtension>()
            var receiverParameterType: KtType? = null
            lateinit var returnType: KtType
            var setterValueParameter: KtValueParameter? = null
            val typeParameters = ArrayList<KtTypeParameter>()
            var versionRequirement: KtVersionRequirement? = null
            lateinit var self: KtProperty
            val lazySelf = { self }

            override fun visitEnd() {
                val propertyFlags = KtPropertiesFlags(flags)
                val setterFunctionFlags = KtFunctionsFlags(setterFlags)
                val getterFunctionFlags = KtFunctionsFlags(getterFlags)
                self = KtPropertyImpl(
                    propertyFlags,
                    name,
                    setterFunctionFlags,
                    getterFunctionFlags,
                    extensions,
                    receiverParameterType,
                    returnType,
                    setterValueParameter,
                    typeParameters,
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

            override fun visitSetterParameter(flags: Flags, name: String) = KtValueParameterImpl(environment, lazySelf, flags, name) {
                setterValueParameter = it
            }

            override fun visitTypeParameter(flags: Flags, name: String, id: Int, variance: KmVariance) =
                KtTypeParameterImpl(environment, lazySelf, flags, name, id, variance) {
                    typeParameters
                }

            override fun visitVersionRequirement() = KtVersionRequirementImpl {
                versionRequirement = it
            }
        }
    }

    data class KtExtensionImpl(
        override val fieldName: String?,
        override val fieldTypeDesc: String?,
        override val getterDesc: String?,
        override val setterDesc: String?,
        override val syntheticMethodForAnnotations: String?
    ) : KtProperty.KtExtension {
        companion object {
            operator fun invoke(type: KmExtensionType, resultListener: (KtProperty.KtExtension) -> Unit): KmPropertyExtensionVisitor? {
                if (type != JvmPropertyExtensionVisitor.TYPE) {
                    val extension = KtExtensionImpl(null, null, null, null, null)
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
                        val extension = KtExtensionImpl(
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
