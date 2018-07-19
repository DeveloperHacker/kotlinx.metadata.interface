package org.jetbrains.research.impl

import kotlinx.metadata.*
import kotlinx.metadata.jvm.JvmTypeExtensionVisitor
import org.jetbrains.research.elements.KtElement
import org.jetbrains.research.elements.KtEnvironment
import org.jetbrains.research.elements.KtType
import org.jetbrains.research.elements.KtTypeArgument
import org.jetbrains.research.flags.KtDeclarationFlags
import org.jetbrains.research.utlis.KtWrapper

data class KtTypeImpl(
    override val flags: KtDeclarationFlags,
    override val extensions: List<KtType.KtExtension>,
    override val abbreviatedType: KtType?,
    override val outerType: KtType?,
    override val typeFlexibilityId: String?,
    override val flexibleUpperBound: KtType?,
    override val typeArguments: List<KtTypeArgument>,
    override val origin: KtType.Origin,
    override val getParent: () -> KtElement
) : KtType {
    companion object {
        operator fun invoke(
            environment: KtEnvironment,
            parent: () -> KtElement,
            flags: Flags,
            resultListener: (KtType) -> Unit
        ): KmTypeVisitor =
            object : KmTypeVisitor() {
                val extensions = ArrayList<KtType.KtExtension>()
                var abbreviatedType: KtType? = null
                var outerType: KtType? = null
                var typeFlexibilityId: String? = null
                var flexibleUpperBound: KtType? = null
                val typeArguments = ArrayList<KtTypeArgument>()
                lateinit var origin: KtType.Origin
                lateinit var self: KtType
                val lazySelf = { self }

                override fun visitAbbreviatedType(flags: Flags) = KtTypeImpl(environment, parent, flags) {
                    abbreviatedType = it
                }

                override fun visitArgument(flags: Flags, variance: KmVariance) = KtSimpleImpl(environment, lazySelf, flags, variance) {
                    typeArguments.add(it)
                }

                override fun visitStarProjection() {
                    typeArguments.add(KtStarProjectionImpl(lazySelf))
                }

                override fun visitEnd() {
                    val typeFlags = KtDeclarationFlags(flags)
                    self = KtTypeImpl(
                        typeFlags,
                        extensions,
                        abbreviatedType,
                        outerType,
                        typeFlexibilityId,
                        flexibleUpperBound,
                        typeArguments,
                        origin,
                        parent
                    )
                    resultListener(self)
                }

                override fun visitExtensions(type: KmExtensionType) = KtExtensionImpl(type) {
                    extensions.add(it)
                }

                override fun visitFlexibleTypeUpperBound(flags: Flags, typeFlexibilityId: String?) =
                    KtTypeImpl(environment, lazySelf, flags) {
                        flexibleUpperBound = it
                        this.typeFlexibilityId = typeFlexibilityId
                    }

                override fun visitOuterType(flags: Flags) = KtTypeImpl(environment, lazySelf, flags) {
                    outerType = it
                }

                override fun visitClass(name: ClassName) {
                    origin = KtType.Origin.Class(name)
                }

                override fun visitTypeAlias(name: ClassName) {
                    origin = KtType.Origin.TypeAlias(name)
                }

                override fun visitTypeParameter(id: Int) {
                    origin = KtType.Origin.TypeParameter(id)
                }
            }
    }

    data class KtExtensionImpl(override val isRaw: Boolean?, override val annotations: List<KmAnnotation>) : KtType.KtExtension {
        companion object {
            operator fun invoke(type: KmExtensionType, resultListener: (KtType.KtExtension) -> Unit): KmTypeExtensionVisitor? {
                if (type != JvmTypeExtensionVisitor.TYPE) {
                    val extension = KtExtensionImpl(null, emptyList())
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
                        val extension = KtExtensionImpl(isRaw.value, annotations)
                        resultListener(extension)
                    }
                }
            }
        }
    }
}
