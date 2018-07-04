package org.jetbrains.research

import com.google.auto.service.AutoService
import org.jetbrains.research.elements.KtClass
import org.jetbrains.research.environments.KtRoundEnvironment
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class PrettyPrintable

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.jetbrains.research.PrettyPrintable")
@SupportedOptions(PrettyPrintProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@AutoService(Processor::class)
class PrettyPrintProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    private fun init(roundEnvironment: RoundEnvironment): String? {
        val annotatedElements = roundEnvironment.getElementsAnnotatedWith(PrettyPrintable::class.java)
        if (annotatedElements.isEmpty()) return null
        Thread.currentThread().contextClassLoader = javaClass.classLoader
        return processingEnv.options[PrettyPrintProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can't find the target directory for generated Kotlin files.")
            return null
        }
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment): Boolean {
        val kaptKotlinGeneratedDir = init(roundEnvironment) ?: return false
        val environment = KtRoundEnvironment(roundEnvironment)
        Env.debug(environment)
        val (dataClasses, otherClasses) = environment
            .getClassElementsWithAnnotation(PrettyPrintable::class.java)
            .filterIsInstance<KtClass>()
            .partition { it.flags.isData }
        otherClasses.forEach {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Unsupported annotation ${PrettyPrintable::class.java.name} for ${it.name}"
            )
        }
        val ktFile = StringBuilder()
        ktFile.append("package compile\n\n\n")
        for (ktData in dataClasses) {
            val parameters = ktData.constructors.first { it.flags.isPrimary }.valueParameters
            val receiver = ktData.name.replace('/', '.')
            val typeParameters = ktData.typeParameters.joinToString(", ") { "*" }.let { if (it.isNotEmpty()) "<$it>" else it }
            val name = ktData.javaElement.simpleName.toString()
            val parameterStr = parameters.joinToString(",\\n${'$'}{ident2}") { "${it.name} = $${it.name}" }
            val prefix = if (parameters.size == 1) "" else "\\n${'$'}{ident2}"
            val postfix = if (parameters.size == 1) "" else "\\n${'$'}{ident1}"
            ktFile.append("fun $receiver$typeParameters.toPrettyString(ident: Int = 0): String {\n")
            ktFile.append("    val ident1 = \" \".repeat(ident * 4)\n")
            if (parameters.size != 1)
                ktFile.append("    val ident2 = \" \".repeat((ident + 1) * 4)\n")
            ktFile.append("    return \"${'$'}{ident1}$name($prefix$parameterStr$postfix)\"\n")
            ktFile.append("}\n\n")
        }
        File(kaptKotlinGeneratedDir, "Pretty.kt")
            .apply { parentFile.mkdirs() }
            .apply { createNewFile() }
            .writeText(ktFile.toString())
        return true
    }

    object Env {
        fun println(any: Any? = "") = System.err.println(any.toString())
    }

    private fun Env.debug(environment: KtRoundEnvironment) {
        println("------- BEGIN DEBUG SPACE -------")
        val ktClass = environment.findAllKtClassElements()
            .filterIsInstance<KtClass>()
            .first { it.simpleName == "AAA" }
        ktClass.constructors
            .map { it.javaElement to it.valueParameters.map { it.type.origin } }
            .forEach { (cons, types) -> println("$cons, $types") }
        println("------- END DEBUG SPACE -------")
    }
}
