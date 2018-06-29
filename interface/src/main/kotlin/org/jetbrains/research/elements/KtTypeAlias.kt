package org.jetbrains.research.elements

import kotlinx.metadata.Flags
import kotlinx.metadata.KmAnnotation
import kotlinx.metadata.KmTypeAliasVisitor
import kotlinx.metadata.KmVariance
import org.jetbrains.research.elements.flags.KtDeclarationFlags

data class KtTypeAlias(
        val flags: KtDeclarationFlags,
        val name: String,
        val annotations: List<KmAnnotation>,
        val expandedType: KtType,
        val underlyingType: KtType,
        val typeParameters: List<KtTypeParameter>,
        val versionRequirement: KtVersionRequirement?
) {
    companion object {
        operator fun invoke(flags: Flags, name: String, resultListener: (KtTypeAlias) -> Unit) = object : KmTypeAliasVisitor() {
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
                val typeAlias = KtTypeAlias(typeAliasFlags, name, annotations, expandedType, underlyingType, typeParameters, versionRequirement)
                resultListener(typeAlias)
            }

            override fun visitExpandedType(flags: Flags) = KtType(flags) {
                expandedType = it
            }

            override fun visitTypeParameter(flags: Flags, name: String, id: Int, variance: KmVariance) = KtTypeParameter(flags, name, id, variance) {
                typeParameters.add(it)
            }

            override fun visitUnderlyingType(flags: Flags) = KtType(flags) {
                underlyingType = it
            }

            override fun visitVersionRequirement() = KtVersionRequirement {
                versionRequirement = it
            }
        }
    }
}