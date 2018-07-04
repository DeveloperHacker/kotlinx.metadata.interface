package org.jetbrains.research.elements

import kotlinx.metadata.Flags
import kotlinx.metadata.KmPackageVisitor
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.LazyInitializer
import org.jetbrains.research.environments.KtEnvironment
import javax.lang.model.element.Element

class KtMultiFileClassPart(environment: KtEnvironment, javaElement: Element, metadata: KotlinClassMetadata.MultiFileClassPart) :
    KtClassElement<KotlinClassMetadata.MultiFileClassPart>(environment, javaElement, metadata) {

    private val lazyInitializer = LazyInitializer {
        metadata.accept(object : KmPackageVisitor() {
            val properties_ = ArrayList<KtProperty>()
            val functions_ = ArrayList<KtFunction>()
            val typeAliases_ = ArrayList<KtTypeAlias>()

            override fun visitFunction(flags: Flags, name: String) = KtFunction(environment, flags, name) {
                functions_.add(it)
            }

            override fun visitProperty(flags: Flags, name: String, getterFlags: Flags, setterFlags: Flags) =
                KtProperty(environment, flags, name, getterFlags, setterFlags) {
                    properties_.add(it)
                }

            override fun visitTypeAlias(flags: Flags, name: String) = KtTypeAlias(environment, flags, name) {
                typeAliases_.add(it)
            }

            override fun visitEnd() {
                functions = functions_
                properties = properties_
                typeAliases = typeAliases_
            }
        })
    }

    var properties: List<KtProperty> by lazyInitializer.Property()
        private set

    var functions: List<KtFunction> by lazyInitializer.Property()
        private set

    var typeAliases: List<KtTypeAlias> by lazyInitializer.Property()
        private set
}