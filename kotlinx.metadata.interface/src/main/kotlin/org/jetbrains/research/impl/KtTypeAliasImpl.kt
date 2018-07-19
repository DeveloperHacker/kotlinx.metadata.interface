package org.jetbrains.research.impl

import kotlinx.metadata.Flags
import kotlinx.metadata.KmAnnotation
import kotlinx.metadata.KmTypeAliasVisitor
import kotlinx.metadata.KmVariance
import org.jetbrains.research.elements.*
import org.jetbrains.research.flags.KtDeclarationFlags

data class KtTypeAliasImpl(
    override val flags: KtDeclarationFlags,
    override val name: String,
    override val annotations: List<KmAnnotation>,
    override val expandedType: KtType,
    override val underlyingType: KtType,
    override val typeParameters: List<KtTypeParameter>,
    override val versionRequirement: KtVersionRequirement?,
    override val getParent: () -> KtElement
) : KtTypeAlias {
    companion object {
        operator fun invoke(
            environment: KtEnvironment,
            parent: () -> KtElement,
            flags: Flags,
            name: String,
            resultListener: (KtTypeAlias) -> Unit
        ) =
            object : KmTypeAliasVisitor() {
                val annotations = ArrayList<KmAnnotation>()
                lateinit var expandedType: KtType
                lateinit var underlyingType: KtType
                val typeParameters = ArrayList<KtTypeParameter>()
                var versionRequirement: KtVersionRequirement? = null
                lateinit var self: KtTypeAlias
                val lazySelf = { self }

                override fun visitAnnotation(annotation: KmAnnotation) {
                    annotations.add(annotation)
                }

                override fun visitEnd() {
                    val typeAliasFlags = KtDeclarationFlags(flags)
                    self = KtTypeAliasImpl(
                        typeAliasFlags,
                        name,
                        annotations,
                        expandedType,
                        underlyingType,
                        typeParameters,
                        versionRequirement,
                        parent
                    )
                    resultListener(self)
                }

                override fun visitExpandedType(flags: Flags) = KtTypeImpl(environment, lazySelf, flags) {
                    expandedType = it
                }

                override fun visitTypeParameter(flags: Flags, name: String, id: Int, variance: KmVariance) =
                    KtTypeParameterImpl(environment, lazySelf, flags, name, id, variance) {
                        typeParameters.add(it)
                    }

                override fun visitUnderlyingType(flags: Flags) = KtTypeImpl(environment, lazySelf, flags) {
                    underlyingType = it
                }

                override fun visitVersionRequirement() = KtVersionRequirementImpl {
                    versionRequirement = it
                }
            }
    }
}
