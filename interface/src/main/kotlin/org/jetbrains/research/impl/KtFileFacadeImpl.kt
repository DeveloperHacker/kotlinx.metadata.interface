package org.jetbrains.research.impl

import kotlinx.metadata.Flags
import kotlinx.metadata.KmPackageVisitor
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.elements.*
import org.jetbrains.research.utlis.LazyInitializer
import javax.lang.model.element.Element

class KtFileFacadeImpl(
    environment: KtEnvironment,
    override val javaElement: Element,
    override val metadata: KotlinClassMetadata.FileFacade
) : KtFileFacade {

    private val lazyInitializer = LazyInitializer {
        metadata.accept(object : KmPackageVisitor() {
            val properties_ = ArrayList<KtProperty>()
            val functions_ = ArrayList<KtFunction>()
            val typeAliases_ = ArrayList<KtTypeAlias>()

            override fun visitFunction(flags: Flags, name: String) = KtFunctionImpl(environment, flags, name) {
                functions_.add(it)
            }

            override fun visitProperty(flags: Flags, name: String, getterFlags: Flags, setterFlags: Flags) =
                KtPropertyImpl(environment, flags, name, getterFlags, setterFlags) {
                    properties_.add(it)
                }

            override fun visitTypeAlias(flags: Flags, name: String) = KtTypeAliasImpl(environment, flags, name) {
                typeAliases_.add(it)
            }

            override fun visitEnd() {
                functions = functions_
                properties = properties_
                typeAliases = typeAliases_
            }
        })
    }

    override var properties: List<KtProperty> by lazyInitializer.Property()
        private set

    override var functions: List<KtFunction> by lazyInitializer.Property()
        private set

    override var typeAliases: List<KtTypeAlias> by lazyInitializer.Property()
        private set
}