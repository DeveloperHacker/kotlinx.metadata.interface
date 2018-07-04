package org.jetbrains.research.elements

import kotlinx.metadata.*
import kotlinx.metadata.jvm.JvmTypeExtensionVisitor
import org.jetbrains.research.KtWrapper
import org.jetbrains.research.elements.flags.KtDeclarationFlags
import org.jetbrains.research.environments.KtEnvironment

data class KtType(
    val flags: KtDeclarationFlags,
    val extensions: List<KtExtension>,
    val abbreviatedType: KtType?,
    val outerType: KtType?,
    val typeFlexibilityId: String?,
    val flexibleUpperBound: KtType?,
    val typeArguments: List<KtTypeArgument>,
    val origin: Origin
) {
    fun isFlexibleType() = flexibleUpperBound != null

    companion object {
        operator fun invoke(environment: KtEnvironment, flags: Flags, resultListener: (KtType) -> Unit): KmTypeVisitor =
            object : KmTypeVisitor() {
                val extensions = ArrayList<KtExtension>()
                var abbreviatedType: KtType? = null
                var outerType: KtType? = null
                var typeFlexibilityId: String? = null
                var flexibleUpperBound: KtType? = null
                val typeArguments = ArrayList<KtTypeArgument>()
                lateinit var origin: Origin

                override fun visitAbbreviatedType(flags: Flags) = KtType(environment, flags) {
                    abbreviatedType = it
                }

                override fun visitArgument(flags: Flags, variance: KmVariance) = KtTypeArgument.Simple(environment, flags, variance) {
                    typeArguments.add(it)
                }

                override fun visitStarProjection() {
                    typeArguments.add(KtTypeArgument.StarProjection())
                }

                override fun visitEnd() {
                    val typeFlags = KtDeclarationFlags(flags)
                    val type =
                        KtType(typeFlags, extensions, abbreviatedType, outerType, typeFlexibilityId, flexibleUpperBound, typeArguments, origin)
                    resultListener(type)
                }

                override fun visitExtensions(type: KmExtensionType) = KtExtension(type) {
                    extensions.add(it)
                }

                override fun visitFlexibleTypeUpperBound(flags: Flags, typeFlexibilityId: String?) = KtType(environment, flags) {
                    flexibleUpperBound = it
                    this.typeFlexibilityId = typeFlexibilityId
                }

                override fun visitOuterType(flags: Flags) = KtType(environment, flags) {
                    outerType = it
                }

                override fun visitClass(name: ClassName) {
                    origin = Origin.Class(environment, name)
                }

                override fun visitTypeAlias(name: ClassName) {
                    origin = Origin.TypeAlias(environment, name)
                }

                override fun visitTypeParameter(id: Int) {
                    origin = Origin.TypeParameter(id)
                }
            }
    }

    sealed class Origin {
        data class TypeParameter(val id: Int) : Origin()

        class Class(environment: KtEnvironment, val name: ClassName) : Origin() {
            val ktClass by lazy {
                environment.findAllKtClassElements().filterIsInstance<KtClass>().firstOrNull { it.name == name }
            }

            override fun toString() = name
        }

        class TypeAlias(environment: KtEnvironment, val name: ClassName) : Origin() {
            val ktClass by lazy {
                environment.findAllKtClassElements().filterIsInstance<KtClass>().firstOrNull { it.name == name }
            }

            override fun toString() = name
        }
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