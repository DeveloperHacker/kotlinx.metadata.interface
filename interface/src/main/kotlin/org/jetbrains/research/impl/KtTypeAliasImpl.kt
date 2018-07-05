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
    override val versionRequirement: KtVersionRequirement?
) : KtTypeAlias {
    companion object {
        operator fun invoke(environment: KtEnvironment, flags: Flags, name: String, resultListener: (KtTypeAlias) -> Unit) =
            object : KmTypeAliasVisitor() {
                val annotations = ArrayList<KmAnnotation>()
                lateinit var expandedType: KtType
                lateinit var underlyingType: KtType
                val typeParameters = ArrayList<KtTypeParameter>()
                var versionRequirement: KtVersionRequirement? = null

                override fun visitAnnotation(annotation: KmAnnotation) {
                    annotations.add(annotation)
                }

                override fun visitEnd() {
                    val typeAliasFlags = KtDeclarationFlags(flags)
                    val typeAlias =
                        KtTypeAliasImpl(typeAliasFlags, name, annotations, expandedType, underlyingType, typeParameters, versionRequirement)
                    resultListener(typeAlias)
                }

                override fun visitExpandedType(flags: Flags) = KtTypeImpl(environment, flags) {
                    expandedType = it
                }

                override fun visitTypeParameter(flags: Flags, name: String, id: Int, variance: KmVariance) =
                    KtTypeParameterImpl(environment, flags, name, id, variance) {
                        typeParameters.add(it)
                    }

                override fun visitUnderlyingType(flags: Flags) = KtTypeImpl(environment, flags) {
                    underlyingType = it
                }

                override fun visitVersionRequirement() = KtVersionRequirementImpl {
                    versionRequirement = it
                }
            }
    }
}
