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
            val nestedProperties_ = ArrayList<KtProperty>()
            val nestedFunctions_ = ArrayList<KtFunction>()
            val nestedTypeAliases_ = ArrayList<KtTypeAlias>()

            override fun visitFunction(flags: Flags, name: String) = KtFunction(environment, flags, name) {
                nestedFunctions_.add(it)
            }

            override fun visitProperty(flags: Flags, name: String, getterFlags: Flags, setterFlags: Flags) =
                KtProperty(environment, flags, name, getterFlags, setterFlags) {
                    nestedProperties_.add(it)
                }

            override fun visitTypeAlias(flags: Flags, name: String) = KtTypeAlias(environment, flags, name) {
                nestedTypeAliases_.add(it)
            }

            override fun visitEnd() {
                nestedFunctions = nestedFunctions_
                nestedProperties = nestedProperties_
                nestedTypeAliases = nestedTypeAliases_
            }
        })
    }

    var nestedProperties: List<KtProperty> by lazyInitializer.Property()
        private set

    var nestedFunctions: List<KtFunction> by lazyInitializer.Property()
        private set

    var nestedTypeAliases: List<KtTypeAlias> by lazyInitializer.Property()
        private set
}