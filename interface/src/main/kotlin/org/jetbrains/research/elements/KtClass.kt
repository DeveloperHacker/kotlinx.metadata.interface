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
            var primaryConstructor_: KtConstructor? = null
            val constructors_ = ArrayList<KtConstructor>()
            val extensions_ = ArrayList<KtExtension>()
            val enumEntries_ = ArrayList<String>()
            val typeParameters_ = ArrayList<KtTypeParameter>()
            val classes_ = ArrayList<KtClass>()
            val superTypes_ = ArrayList<KtType>()
            val sealedSubclasses_ = ArrayList<KtClass>()
            val typeAliases_ = ArrayList<KtTypeAlias>()
            val functions_ = ArrayList<KtFunction>()
            val properties_ = ArrayList<KtProperty>()
            var versionRequirement_: KtVersionRequirement? = null

            val javaConstructors = javaElement.enclosedElements.filter { it.kind == ElementKind.CONSTRUCTOR }

            override fun visit(flags: Flags, name: ClassName) {
                flags_ = KtClassFlags(flags)
                name_ = name
            }

            override fun visitTypeParameter(flags: Flags, name: String, id: Int, variance: KmVariance) =
                KtTypeParameter(environment, flags, name, id, variance) {
                    typeParameters_.add(it)
                }

            override fun visitSupertype(flags: Flags) = KtType(environment, flags) {
                superTypes_.add(it)
            }

            override fun visitCompanionObject(name: String) = KtClass(environment, javaElement, name) {
                companion_ = it
            }

            override fun visitConstructor(flags: Flags) = KtConstructor(environment, flags) {
                if (it.flags.isPrimary) primaryConstructor_ = it
                constructors_.add(it)
            }

            override fun visitEnumEntry(name: String) {
                enumEntries_.add(name)
            }

            override fun visitExtensions(type: KmExtensionType) = KtExtension(type) {
                extensions_.add(it)
            }

            override fun visitNestedClass(name: String) = KtClass(environment, javaElement, name) {
                classes_.add(it)
            }

            override fun visitSealedSubclass(name: ClassName) = KtClass(environment, javaElement, name) {
                sealedSubclasses_.add(it)
            }

            override fun visitVersionRequirement() = KtVersionRequirement {
                versionRequirement_ = it
            }

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
                val (javaConstructors, kotlinConstructors) = if (primaryConstructor_ != null) {
                    constructors_.last().javaElement = javaConstructors.first()
                    Pair(javaConstructors.drop(1), constructors_.dropLast(1))
                } else {
                    Pair(javaConstructors, constructors_)
                }
                for ((java, kotlin) in javaConstructors.zip(kotlinConstructors)) {
                    kotlin.javaElement = java
                }
                flags = flags_
                name = name_
                companion = companion_
                primaryConstructor = primaryConstructor_
                constructors = constructors_
                extensions = extensions_
                enumEntries = enumEntries_
                versionRequirement = versionRequirement_
                typeParameters = typeParameters_
                classes = classes_
                superTypes = superTypes_
                sealedSubclasses = sealedSubclasses_
                functions = functions_
                properties = properties_
                typeAliases = typeAliases_
            }
        })
    }

    var flags: KtClassFlags by lazyInitializer.Property()
        private set

    var name: ClassName by lazyInitializer.Property()
        private set

    var companion: KtClass? by lazyInitializer.Property()
        private set

    var primaryConstructor: KtConstructor? by lazyInitializer.Property()
        private set

    var constructors: List<KtConstructor> by lazyInitializer.Property()
        private set

    var extensions: List<KtExtension> by lazyInitializer.Property()
        private set

    var enumEntries: List<String> by lazyInitializer.Property()
        private set

    var typeParameters: List<KtTypeParameter> by lazyInitializer.Property()
        private set

    var classes: List<KtClass> by lazyInitializer.Property()
        private set

    var superTypes: List<KtType> by lazyInitializer.Property()
        private set

    var sealedSubclasses: List<KtClass> by lazyInitializer.Property()
        private set

    var versionRequirement: KtVersionRequirement? by lazyInitializer.Property()
        private set

    var typeAliases: List<KtTypeAlias> by lazyInitializer.Property()
        private set

    var functions: List<KtFunction> by lazyInitializer.Property()
        private set

    var properties: List<KtProperty> by lazyInitializer.Property()
        private set

    companion object {
        operator fun invoke(environment: KtEnvironment, javaElement: Element, name: ClassName, resultListener: (KtClass) -> Unit) =
            resultListener(
                javaElement.enclosedElements.asSequence()
                    .mapNotNull { environment.getKtClassElement(it) }
                    .filterIsInstance<KtClass>()
                    .first { it.javaElement.simpleName.toString() == name })
    }

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