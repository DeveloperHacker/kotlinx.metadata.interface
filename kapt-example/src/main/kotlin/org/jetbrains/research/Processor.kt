package org.jetbrains.research

import com.google.auto.service.AutoService
import org.jetbrains.research.elements.KtClass
import org.jetbrains.research.elements.KtFileFacade
import org.jetbrains.research.environments.KtEnvironment
import org.jetbrains.research.environments.KtRoundEnvironment
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class TestAnnotation

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.jetbrains.research.TestAnnotation")
@SupportedOptions(TestAnnotationProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@AutoService(Processor::class)
class TestAnnotationProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    class Env(private val file: File, private val indent: Int = 1, prefix: String = "") {
        private var prefix: String = prefix
            get() = field.also { field = "" }

        fun println(any: Any? = "") = file.appendText("     ".repeat(indent) + prefix + any.toString() + "\n")
        fun printLn(any: Any? = "") = file.appendText("     ".repeat(indent - 1) + prefix + any.toString() + "\n")
        fun child(prefix: String = "") = Env(file, indent + 1, prefix)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment): Boolean {
        Thread.currentThread().contextClassLoader = javaClass.classLoader
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can't find the target directory for generated Kotlin files.")
            return false
        }
        val file = File(kaptKotlinGeneratedDir, "structure.txt")
                .apply { parentFile.mkdirs() }
                .apply { createNewFile() }
        val environment = KtRoundEnvironment(roundEnvironment)
        val printEnvironment = Env(file)
        environment.getRootClassElements().forEach {
            when (it) {
                is KtClass -> printEnvironment.process(environment, it)
                is KtFileFacade -> println(it)
            }
        }
        return true
    }

    private fun Env.process(environment: KtEnvironment, ktClass: KtClass) {
        printLn("class ${ktClass.name} ${ktClass.flags} {")
        ktClass.enumEntries.let { if (it.isNotEmpty()) println("enumEntries=$it") }
        ktClass.extensions.forEach { println(it) }
        ktClass.constructors.forEach { println(it) }
        ktClass.companion?.let { child("companion ").process(environment, it) }
        ktClass.versionRequirement?.let { println("versionRequirement=$it") }
        printLn("}")
    }
}