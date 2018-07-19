package org.jetbrains.research.impl

import kotlinx.metadata.*
import kotlinx.metadata.jvm.JvmClassExtensionVisitor
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.elements.*
import org.jetbrains.research.flags.KtClassFlags
import org.jetbrains.research.utlis.LazyInitializer
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

class KtClassImpl(
    environment: KtEnvironment,
    override val javaElement: Element,
    override val metadata: KotlinClassMetadata.Class,
    override val getParent: () -> KtElement?
) : KtClass {

    private val lazyInitializer = LazyInitializer {
        metadata.accept(object : KmClassVisitor() {
            lateinit var flags_: KtClassFlags
            lateinit var name_: ClassName
            var companion_: KtClass? = null
            var primaryConstructor_: KtConstructor? = null
            val constructors_ = ArrayList<KtConstructorImpl>()
            val extensions_ = ArrayList<KtClass.KtExtension>()
            val enumEntries_ = ArrayList<String>()
            val typeParameters_ = ArrayList<KtTypeParameter>()
            val classes_ = ArrayList<KtClass>()
            val superTypes_ = ArrayList<KtType>()
            val sealedSubclasses_ = ArrayList<KtClass>()
            val typeAliases_ = ArrayList<KtTypeAlias>()
            val functions_ = ArrayList<KtFunctionImpl>()
            val properties_ = ArrayList<KtProperty>()
            var versionRequirement_: KtVersionRequirement? = null
            val lazySelf = { this@KtClassImpl }


            override fun visit(flags: Flags, name: ClassName) {
                flags_ = KtClassFlags(flags)
                name_ = name
            }

            override fun visitTypeParameter(flags: Flags, name: String, id: Int, variance: KmVariance) =
                KtTypeParameterImpl(environment, lazySelf, flags, name, id, variance) {
                    typeParameters_.add(it)
                }

            override fun visitSupertype(flags: Flags) = KtTypeImpl(environment, lazySelf, flags) {
                superTypes_.add(it)
            }

            override fun visitCompanionObject(name: String) = KtClassImpl(environment, javaElement, name) {
                companion_ = it
            }

            override fun visitConstructor(flags: Flags) = KtConstructorImpl(environment, lazySelf, flags) {
                if (it.flags.isPrimary) primaryConstructor_ = it
                constructors_.add(it)
            }

            override fun visitEnumEntry(name: String) {
                enumEntries_.add(name)
            }

            override fun visitExtensions(type: KmExtensionType) = KtExtensionImpl(type) {
                extensions_.add(it)
            }

            override fun visitNestedClass(name: String) = KtClassImpl(environment, javaElement, name) {
                classes_.add(it)
            }

            override fun visitSealedSubclass(name: ClassName) = KtClassImpl(environment, javaElement, name) {
                sealedSubclasses_.add(it)
            }

            override fun visitVersionRequirement() = KtVersionRequirementImpl {
                versionRequirement_ = it
            }

            override fun visitFunction(flags: Flags, name: String) = KtFunctionImpl(environment, lazySelf, flags, name) {
                functions_.add(it)
            }

            override fun visitProperty(flags: Flags, name: String, getterFlags: Flags, setterFlags: Flags) =
                KtPropertyImpl(environment, lazySelf, flags, name, getterFlags, setterFlags) {
                    properties_.add(it)
                }

            override fun visitTypeAlias(flags: Flags, name: String) = KtTypeAliasImpl(environment, lazySelf, flags, name) {
                typeAliases_.add(it)
            }

            override fun visitEnd() {
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

                val javaConstructors = javaElement.enclosedElements
                    .filter { it.kind == ElementKind.CONSTRUCTOR }
                    .map { IdentityFunction.valueOf(it) to it }
                    .toMap()
                for (constructor in constructors_) {
                    val identityFunction = IdentityFunction.valueOf(constructor)
                    val element = javaConstructors[identityFunction] ?: continue
                    constructor.javaElement = element
                    environment.cache(constructor)
                }
                val javaFunctions = javaElement.enclosedElements
                    .filter { it.kind == ElementKind.METHOD }
                    .map { IdentityFunction.valueOf(it) to it }
                    .toMap()
                for (function in functions_) {
                    val identityFunction = IdentityFunction.valueOf(function)
                    val element = javaFunctions[identityFunction] ?: continue
                    function.javaElement = element
                    environment.cache(function)
                }
            }
        })
    }

    override fun toString() = javaElement.toString()

    override var flags: KtClassFlags by lazyInitializer.Property()
        private set

    override var name: ClassName by lazyInitializer.Property()
        private set

    override var companion: KtClass? by lazyInitializer.Property()
        private set

    override var primaryConstructor: KtConstructor? by lazyInitializer.Property()
        private set

    override var constructors: List<KtConstructor> by lazyInitializer.Property()
        private set

    override var extensions: List<KtClass.KtExtension> by lazyInitializer.Property()
        private set

    override var enumEntries: List<String> by lazyInitializer.Property()
        private set

    override var typeParameters: List<KtTypeParameter> by lazyInitializer.Property()
        private set

    override var classes: List<KtClass> by lazyInitializer.Property()
        private set

    override var superTypes: List<KtType> by lazyInitializer.Property()
        private set

    override var sealedSubclasses: List<KtClass> by lazyInitializer.Property()
        private set

    override var versionRequirement: KtVersionRequirement? by lazyInitializer.Property()
        private set

    override var typeAliases: List<KtTypeAlias> by lazyInitializer.Property()
        private set

    override var functions: List<KtFunction> by lazyInitializer.Property()
        private set

    override var properties: List<KtProperty> by lazyInitializer.Property()
        private set

    override fun forceInit() = lazyInitializer.forceInit()

    companion object {
        operator fun invoke(environment: KtEnvironment, javaElement: Element, name: ClassName, resultListener: (KtClass) -> Unit) =
            resultListener(
                javaElement.enclosedElements.asSequence()
                    .mapNotNull { environment.getKtElement(it) }
                    .filterIsInstance<KtClass>()
                    .first { it.javaElement.simpleName.toString() == name })
    }

    data class KtExtensionImpl(override val anonymousObjectOriginName: String?) : KtClass.KtExtension {
        companion object {
            operator fun invoke(type: KmExtensionType, resultListener: (KtClass.KtExtension) -> Unit): KmClassExtensionVisitor? {
                if (type != JvmClassExtensionVisitor.TYPE) {
                    val extension = KtExtensionImpl(null)
                    resultListener(extension)
                    return null
                }
                return object : JvmClassExtensionVisitor() {
                    override fun visitAnonymousObjectOriginName(internalName: String) {
                        val extension = KtExtensionImpl(internalName)
                        resultListener(extension)
                    }
                }
            }
        }
    }
}
