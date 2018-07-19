package org.jetbrains.research.impl

import kotlinx.metadata.Flags
import kotlinx.metadata.KmConstructorExtensionVisitor
import kotlinx.metadata.KmConstructorVisitor
import kotlinx.metadata.KmExtensionType
import kotlinx.metadata.jvm.JvmConstructorExtensionVisitor
import org.jetbrains.research.elements.*
import org.jetbrains.research.flags.KtConstructorsFlags
import javax.lang.model.element.Element

data class KtConstructorImpl(
    override val flags: KtConstructorsFlags,
    override val extensions: List<KtConstructor.KtExtension>,
    override val valueParameters: List<KtValueParameter>,
    override val versionRequirement: KtVersionRequirement?,
    override val getParent: () -> KtClass
) : KtConstructor {

    override var javaElement: Element? = null
        internal set

    companion object {
        operator fun invoke(environment: KtEnvironment, parent: () -> KtClass, flags: Flags, resultListener: (KtConstructorImpl) -> Unit) =
            object : KmConstructorVisitor() {
                val extensions = ArrayList<KtConstructor.KtExtension>()
                val valueParameters = ArrayList<KtValueParameter>()
                var versionRequirement: KtVersionRequirement? = null
                lateinit var self: KtConstructorImpl
                val lazySelf = { self }

                override fun visitEnd() {
                    val constructorFlags = KtConstructorsFlags(flags)
                    self = KtConstructorImpl(constructorFlags, extensions, valueParameters, versionRequirement, parent)
                    resultListener(self)
                }

                override fun visitExtensions(type: KmExtensionType) = KtExtensionImpl(type) {
                    extensions.add(it)
                }

                override fun visitValueParameter(flags: Flags, name: String) = KtValueParameterImpl(environment, lazySelf, flags, name) {
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
