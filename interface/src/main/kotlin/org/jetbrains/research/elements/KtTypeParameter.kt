package org.jetbrains.research.elements

import kotlinx.metadata.*
import kotlinx.metadata.jvm.JvmTypeParameterExtensionVisitor
import org.jetbrains.research.elements.flags.KtDeclarationFlags

data class KtTypeParameter(
        val flags: KtDeclarationFlags,
        val name: String,
        val id: Int,
        val variance: KmVariance,
        val extensions: List<KtExtension>,
        val upperBounds: List<KtType>
) {
    companion object {
        operator fun invoke(flags: Flags, name: String, id: Int, variance: KmVariance, resultListener: (KtTypeParameter) -> Unit) = object : KmTypeParameterVisitor() {
            val extensions = ArrayList<KtExtension>()
            val upperBounds = ArrayList<KtType>()

            override fun visitEnd() {
                val typeFlags = KtDeclarationFlags(flags)
                val typeParameter = KtTypeParameter(typeFlags, name, id, variance, extensions, upperBounds)
                resultListener(typeParameter)
            }

            override fun visitExtensions(type: KmExtensionType) = KtExtension(type) {
                extensions.add(it)
            }

            override fun visitUpperBound(flags: Flags) = KtType(flags) {
                upperBounds.add(it)
            }
        }
    }

    data class KtExtension(val annotations: List<KmAnnotation>) {
        companion object {
            operator fun invoke(type: KmExtensionType, resultListener: (KtExtension) -> Unit): KmTypeParameterExtensionVisitor? {
                if (type != JvmTypeParameterExtensionVisitor.TYPE) {
                    val extension = KtExtension(emptyList())
                    resultListener(extension)
                    return null
                }
                return object : JvmTypeParameterExtensionVisitor() {
                    val annotations = ArrayList<KmAnnotation>()

                    override fun visitAnnotation(annotation: KmAnnotation) {
                        annotations.add(annotation)
                    }

                    override fun visitEnd() {
                        val extension = KtExtension(annotations)
                        resultListener(extension)
                    }
                }
            }
        }
    }
}
