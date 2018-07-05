package org.jetbrains.research.impl

import kotlinx.metadata.Flags
import kotlinx.metadata.KmConstructorExtensionVisitor
import kotlinx.metadata.KmConstructorVisitor
import kotlinx.metadata.KmExtensionType
import kotlinx.metadata.jvm.JvmConstructorExtensionVisitor
import org.jetbrains.research.elements.KtConstructor
import org.jetbrains.research.elements.KtEnvironment
import org.jetbrains.research.elements.KtValueParameter
import org.jetbrains.research.elements.KtVersionRequirement
import org.jetbrains.research.flags.KtConstructorsFlags

data class KtConstructorImpl(
    override val flags: KtConstructorsFlags,
    override val extensions: List<KtConstructor.KtExtension>,
    override val valueParameters: List<KtValueParameter>,
    override val versionRequirement: KtVersionRequirement?
) : KtConstructor {

    companion object {
        operator fun invoke(environment: KtEnvironment, flags: Flags, resultListener: (KtConstructor) -> Unit) =
            object : KmConstructorVisitor() {
                val extensions = ArrayList<KtConstructor.KtExtension>()
                val valueParameters = ArrayList<KtValueParameter>()
                var versionRequirement: KtVersionRequirement? = null

                override fun visitEnd() {
                    val constructorFlags = KtConstructorsFlags(flags)
                    val constructor = KtConstructorImpl(constructorFlags, extensions, valueParameters, versionRequirement)
                    resultListener(constructor)
                }

                override fun visitExtensions(type: KmExtensionType) = KtExtensionImpl(type) {
                    extensions.add(it)
                }

                override fun visitValueParameter(flags: Flags, name: String) = KtValueParameterImpl(environment, flags, name) {
                    valueParameters.add(it)
                }

                override fun visitVersionRequirement() = KtVersionRequirementImpl {
                    versionRequirement = it
                }
            }
    }

    data class KtExtensionImpl(override val descriptor: String?) : KtConstructor.KtExtension {
        companion object {
            operator fun invoke(
                type: KmExtensionType,
                resultListener: (KtConstructor.KtExtension) -> Unit
            ): KmConstructorExtensionVisitor? {
                if (type != JvmConstructorExtensionVisitor.TYPE) {
                    val extension = KtExtensionImpl(null)
                    resultListener(extension)
                    return null
                }
                return object : JvmConstructorExtensionVisitor() {
                    override fun visit(desc: String?) {
                        val extension = KtExtensionImpl(desc)
                        resultListener(extension)
                    }
                }
            }
        }
    }
}
