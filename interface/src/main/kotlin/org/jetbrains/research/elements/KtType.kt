package org.jetbrains.research.elements

import kotlinx.metadata.*
import kotlinx.metadata.jvm.JvmTypeExtensionVisitor
import org.jetbrains.research.KtWrapper
import org.jetbrains.research.elements.flags.KtDeclarationFlags

data class KtType(
        val flags: KtDeclarationFlags,
        val extensions: List<KtExtension>,
        val abbreviatedType: KtType?,
        val outerType: KtType?,
        val typeFlexibilityId: String?,
        val flexibleUpperBound: KtType?,
        val typeArguments: List<KtTypeArgument>
) {
    fun isFlexibleType() = flexibleUpperBound != null

    companion object {
        operator fun invoke(flags: Flags, resultListener: (KtType) -> Unit): KmTypeVisitor = object : KmTypeVisitor() {
            val extensions = ArrayList<KtExtension>()
            var abbreviatedType: KtType? = null
            var outerType: KtType? = null
            var typeFlexibilityId: String? = null
            var flexibleUpperBound: KtType? = null
            val typeArguments = ArrayList<KtTypeArgument>()
            lateinit var origin: Origin

            override fun visitAbbreviatedType(flags: Flags) = KtType(flags) {
                abbreviatedType = it
            }

            override fun visitArgument(flags: Flags, variance: KmVariance) = KtTypeArgument.Simple(flags, variance) {
                typeArguments.add(it)
            }

            override fun visitStarProjection() {
                typeArguments.add(KtTypeArgument.StarProjection())
            }

            override fun visitEnd() {
                val typeFlags = KtDeclarationFlags(flags)
                val type = KtType(typeFlags, extensions, abbreviatedType, outerType, typeFlexibilityId, flexibleUpperBound, typeArguments)
                resultListener(type)
            }

            override fun visitExtensions(type: KmExtensionType) = KtExtension(type) {
                extensions.add(it)
            }

            override fun visitFlexibleTypeUpperBound(flags: Flags, typeFlexibilityId: String?) = KtType(flags) {
                flexibleUpperBound = it
                this.typeFlexibilityId = typeFlexibilityId
            }

            override fun visitOuterType(flags: Flags) = KtType(flags) {
                outerType = it
            }

            override fun visitClass(name: ClassName) {
                origin = Origin.Class(name)
            }

            override fun visitTypeAlias(name: ClassName) {
                origin = Origin.TypeAlias(name)
            }

            override fun visitTypeParameter(id: Int) {
                origin = Origin.TypeParameter(id)
            }
        }
    }

    sealed class Origin {
        data class Class(val name: ClassName) : Origin()
        data class TypeAlias(val name: ClassName) : Origin()
        data class TypeParameter(val id: Int) : Origin()
    }

    data class KtExtension(val isRaw: Boolean?, val annotations: List<KmAnnotation>) {
        companion object {
            operator fun invoke(type: KmExtensionType, resultListener: (KtExtension) -> Unit): KmTypeExtensionVisitor? {
                if (type != JvmTypeExtensionVisitor.TYPE) {
                    val extension = KtExtension(null, emptyList())
                    resultListener(extension)
                    return null
                }
                return object : JvmTypeExtensionVisitor() {
                    lateinit var isRaw: KtWrapper<Boolean>
                    val annotations = ArrayList<KmAnnotation>()

                    override fun visit(isRaw: Boolean) {
                        this.isRaw = KtWrapper(isRaw)
                    }

                    override fun visitAnnotation(annotation: KmAnnotation) {
                        annotations.add(annotation)
                    }

                    override fun visitEnd() {
                        val extension = KtExtension(isRaw.value, annotations)
                        resultListener(extension)
                    }
                }
            }
        }
    }
}