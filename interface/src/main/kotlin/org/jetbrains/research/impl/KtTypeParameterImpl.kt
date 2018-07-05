package org.jetbrains.research.impl

import kotlinx.metadata.*
import kotlinx.metadata.jvm.JvmTypeParameterExtensionVisitor
import org.jetbrains.research.elements.KtEnvironment
import org.jetbrains.research.elements.KtType
import org.jetbrains.research.elements.KtTypeParameter
import org.jetbrains.research.flags.KtDeclarationFlags

data class KtTypeParameterImpl(
    override val flags: KtDeclarationFlags,
    override val name: String,
    override val id: Int,
    override val variance: KmVariance,
    override val extensions: List<KtTypeParameter.KtExtension>,
    override val upperBounds: List<KtType>
) : KtTypeParameter {
    companion object {
        operator fun invoke(
            environment: KtEnvironment,
            flags: Flags,
            name: String,
            id: Int,
            variance: KmVariance,
            resultListener: (KtTypeParameter) -> Unit
        ) = object : KmTypeParameterVisitor() {
            val extensions = ArrayList<KtTypeParameter.KtExtension>()
            val upperBounds = ArrayList<KtType>()

            override fun visitEnd() {
                val typeFlags = KtDeclarationFlags(flags)
                val typeParameter = KtTypeParameterImpl(typeFlags, name, id, variance, extensions, upperBounds)
                resultListener(typeParameter)
            }

            override fun visitExtensions(type: KmExtensionType) = KtExtensionImpl(type) {
                extensions.add(it)
            }

            override fun visitUpperBound(flags: Flags) = KtTypeImpl(environment, flags) {
                upperBounds.add(it)
            }
        }
    }

    data class KtExtensionImpl(override val annotations: List<KmAnnotation>) : KtTypeParameter.KtExtension {
        companion object {
            operator fun invoke(
                type: KmExtensionType,
                resultListener: (KtTypeParameter.KtExtension) -> Unit
            ): KmTypeParameterExtensionVisitor? {
                if (type != JvmTypeParameterExtensionVisitor.TYPE) {
                    val extension = KtExtensionImpl(emptyList())
                    resultListener(extension)
                    return null
                }
                return object : JvmTypeParameterExtensionVisitor() {
                    val annotations = ArrayList<KmAnnotation>()

                    override fun visitAnnotation(annotation: KmAnnotation) {
                        annotations.add(annotation)
                    }

                    override fun visitEnd() {
                        val extension = KtExtensionImpl(annotations)
                        resultListener(extension)
                    }
                }
            }
        }
    }
}
