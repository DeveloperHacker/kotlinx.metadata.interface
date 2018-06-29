package org.jetbrains.research.elements

import kotlinx.metadata.Flags
import kotlinx.metadata.KmConstructorExtensionVisitor
import kotlinx.metadata.KmConstructorVisitor
import kotlinx.metadata.KmExtensionType
import kotlinx.metadata.jvm.JvmConstructorExtensionVisitor
import org.jetbrains.research.elements.flags.KtConstructorsFlags

data class KtConstructor(
        val flags: KtConstructorsFlags,
        val extensions: List<KtExtension>,
        val valueParameters: List<KtValueParameter>,
        val versionRequirement: KtVersionRequirement?
) {
    companion object {
        operator fun invoke(flags: Flags, resultListener: (KtConstructor) -> Unit) = object : KmConstructorVisitor() {
            val extensions = ArrayList<KtExtension>()
            val valueParameters = ArrayList<KtValueParameter>()
            var versionRequirement: KtVersionRequirement? = null

            override fun visitEnd() {
                val constructorFlags = KtConstructorsFlags(flags)
                val constructor = KtConstructor(constructorFlags, extensions, valueParameters, versionRequirement)
                resultListener(constructor)
            }

            override fun visitExtensions(type: KmExtensionType) = KtExtension(type) {
                extensions.add(it)
            }

            override fun visitValueParameter(flags: Flags, name: String) = KtValueParameter(flags, name) {
                valueParameters.add(it)
            }

            override fun visitVersionRequirement() = KtVersionRequirement {
                versionRequirement = it
            }
        }
    }

    data class KtExtension(val descriptor: String?) {
        companion object {
            operator fun invoke(type: KmExtensionType, resultListener: (KtExtension) -> Unit): KmConstructorExtensionVisitor? {
                if (type != JvmConstructorExtensionVisitor.TYPE) {
                    val extension = KtExtension(null)
                    resultListener(extension)
                    return null
                }
                return object : JvmConstructorExtensionVisitor() {
                    override fun visit(desc: String?) {
                        val extension = KtExtension(desc)
                        resultListener(extension)
                    }
                }
            }
        }
    }
}