package org.jetbrains.research

import com.google.auto.service.AutoService
import org.jetbrains.research.elements.*
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.jetbrains.research.Getter")
@AutoService(Processor::class)
class GetterProcessor : KtAbstractProcessor<Getter>(Getter::class.java) {

    override fun process(kaptKotlinGeneratedDir: String, environment: KtEnvironment) {
//        environment as KtEnvironmentImpl
//        val annotated = environment.roundEnvironment.getElementsAnnotatedWith(Getter::class.java).first()
//        val ktElement =
//        System.err.println(annotated)
//        System.err.println(environment.getKtElement(annotated))
//        with(environment) {
//            System.err.println((annotated.getNearestClassElement() as? KtClass)?.functions)
//        }
        System.err.println(environment.getKtElements(Getter::class.java).toList())
        val (getters, other) = environment.getKtElements(Getter::class.java).partition { it is KtFunction }
        other.forEach { error("Unsupported annotation ${Getter::class.java.name} for ${it::class.java.name}") }
        @Suppress("UNCHECKED_CAST")
        getters as List<KtFunction>
        val ktFile = StringBuilder()
        ktFile.append("package compile\n\n\n")
        for (getter in getters) {
            if (!getter.flags.visibility.isPublic) error("Getter function must be a public function")
            if (getter.valueParameters.isNotEmpty()) error("Getter function must be a function without arguments")
            if (getter.receiverParameterType != null) error("Getter function must be a function without arguments")
            val annotation = getter.getAnnotation(Getter::class.java) ?: error("wtf?")
            val name = annotation.name
            if (!name.matches("[a-z_A-Z]+".toRegex())) error("Getter name must be in simple java name format")
            val getterType = getter.returnType.origin
            val type = when (getterType) {
                is KtType.Origin.TypeParameter -> getter.allTypeParameters.first { it.id == getterType.id }.name
                is KtType.Origin.TypeAlias -> getterType.name
                is KtType.Origin.Class -> getterType.name
            }
            val propertyReceiver = (getter.getParent() as? KtClass)?.name ?: error("Getter receiver hasn't recognised")
            ktFile.append("val $propertyReceiver.$name: $type\n")
            ktFile.append("    get() = ${getter.name}\n\n")
        }
        File(kaptKotlinGeneratedDir, "Getters.kt")
            .apply { parentFile.mkdirs() }
            .apply { createNewFile() }
            .writeText(ktFile.toString())
    }
}