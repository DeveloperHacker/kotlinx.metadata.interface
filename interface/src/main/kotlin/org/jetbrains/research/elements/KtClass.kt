package org.jetbrains.research.elements

import kotlinx.metadata.*
import kotlinx.metadata.jvm.JvmClassExtensionVisitor
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.LazyInitializer
import org.jetbrains.research.elements.flags.KtClassFlags
import org.jetbrains.research.environments.KtEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

class KtClass(environment: KtEnvironment, javaElement: Element, metadata: KotlinClassMetadata.Class) :
    KtClassElement<KotlinClassMetadata.Class>(environment, javaElement, metadata) {

    private val lazyInitializer = LazyInitializer {
        metadata.accept(object : KmClassVisitor() {
            lateinit var flags_: KtClassFlags
            lateinit var name_: ClassName
            var companion_: KtClass? = null
            val constructors_ = ArrayList<KtConstructor>()
            val extensions_ = ArrayList<KtExtension>()
            val enumEntries_ = ArrayList<String>()
            val typeParameters_ = ArrayList<KtTypeParameter>()
            val nestedClasses_ = ArrayList<KtClass>()
            val superTypes_ = ArrayList<KtType>()
            val sealedSubclasses_ = ArrayList<KtClass>()
            val nestedTypeAliases_ = ArrayList<KtTypeAlias>()
            val nestedFunctions_ = ArrayList<KtFunction>()
            val nestedProperties_ = ArrayList<KtProperty>()
            var versionRequirement_: KtVersionRequirement? = null

            val enclosedClasses: List<KtClass> by lazy {
                javaElement.enclosedElements
                    .filter { it.kind == ElementKind.CLASS }
                    .mapNotNull { environment.getKtClassElement(it) }
                    .filterIsInstance<KtClass>()
            }

            override fun visit(flags: Flags, name: ClassName) {
                flags_ = KtClassFlags(flags)
                name_ = name
            }

            override fun visitCompanionObject(name: String) {
                companion_ = enclosedClasses.first { it.javaElement.simpleName.toString() == name }
            }

            override fun visitConstructor(flags: Flags) = KtConstructor(environment, flags) {
                constructors_.add(it)
            }

            override fun visitEnumEntry(name: String) {
                enumEntries_.add(name)
            }

            override fun visitExtensions(type: KmExtensionType) = KtExtension(type) {
                extensions_.add(it)
            }

            override fun visitNestedClass(name: String) {
                val nestedClass = enclosedClasses.first { it.javaElement.simpleName.toString() == name }
                nestedClasses_.add(nestedClass)
            }

            override fun visitSealedSubclass(name: ClassName) {
                val sealedSubclass = enclosedClasses.first { it.javaElement.simpleName.toString() == name }
                sealedSubclasses_.add(sealedSubclass)
            }

            override fun visitSupertype(flags: Flags) = KtType(environment, flags) {
                superTypes_.add(it)
            }

            override fun visitTypeParameter(flags: Flags, name: String, id: Int, variance: KmVariance) =
                KtTypeParameter(environment, flags, name, id, variance) {
                    typeParameters_.add(it)
                }

            override fun visitVersionRequirement() = KtVersionRequirement {
                versionRequirement_ = it
            }

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
                flags = flags_
                name = name_
                companion = companion_
                constructors = constructors_
                extensions = extensions_
                enumEntries = enumEntries_
                versionRequirement = versionRequirement_
                typeParameters = typeParameters_
                nestedClasses = nestedClasses_
                superTypes = superTypes_
                sealedSubclasses = sealedSubclasses_
                nestedFunctions = nestedFunctions_
                nestedProperties = nestedProperties_
                nestedTypeAliases = nestedTypeAliases_
            }
        })
    }

    var flags: KtClassFlags by lazyInitializer.Property()
        private set

    var name: ClassName by lazyInitializer.Property()
        private set

    var companion: KtClass? by lazyInitializer.Property()
        private set

    var constructors: List<KtConstructor> by lazyInitializer.Property()
        private set

    var extensions: List<KtExtension> by lazyInitializer.Property()
        private set

    var enumEntries: List<String> by lazyInitializer.Property()
        private set

    var typeParameters: List<KtTypeParameter> by lazyInitializer.Property()
        private set

    var nestedClasses: List<KtClass> by lazyInitializer.Property()
        private set

    var superTypes: List<KtType> by lazyInitializer.Property()
        private set

    var sealedSubclasses: List<KtClass> by lazyInitializer.Property()
        private set

    var versionRequirement: KtVersionRequirement? by lazyInitializer.Property()
        private set

    var nestedTypeAliases: List<KtTypeAlias> by lazyInitializer.Property()
        private set

    var nestedFunctions: List<KtFunction> by lazyInitializer.Property()
        private set

    var nestedProperties: List<KtProperty> by lazyInitializer.Property()
        private set

    data class KtExtension(val anonymousObjectOriginName: String?) {
        companion object {
            operator fun invoke(type: KmExtensionType, resultListener: (KtExtension) -> Unit): KmClassExtensionVisitor? {
                if (type != JvmClassExtensionVisitor.TYPE) {
                    val extension = KtExtension(null)
                    resultListener(extension)
                    return null
                }
                return object : JvmClassExtensionVisitor() {
                    override fun visitAnonymousObjectOriginName(internalName: String) {
                        val extension = KtExtension(internalName)
                        resultListener(extension)
                    }
                }
            }
        }
    }
}