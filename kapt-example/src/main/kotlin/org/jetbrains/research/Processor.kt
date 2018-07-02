package org.jetbrains.research

import com.google.auto.service.AutoService
import org.jetbrains.research.elements.*
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

    class Env(private val file: File, private val indent: Int, prefix: String = "") {
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
        val printEnvironment = Env(file, 0)
        environment.getRootClassElements().forEach {
            when (it) {
                is KtClass -> printEnvironment.child("class ").process(it)
                is KtFileFacade -> printEnvironment.child().process(it)
                is KtSyntheticClass -> printEnvironment.child().process(it)
                is KtMultiFileClassFacade -> printEnvironment.child().printLn("multi file facade")
                is KtMultiFileClassPart -> printEnvironment.child().process(it)
                is KtUnknown -> printEnvironment.child().printLn("unknown")
            }
        }
        return true
    }

    private fun Env.process(element: KtSyntheticClass) {
        printLn("file facade {")
        println(element.function)
        printLn("}")
    }

    private fun Env.process(element: KtMultiFileClassPart) {
        printLn("multi file part {")
        element.nestedProperties.forEach { child("nested property ").printLn(it) }
        element.nestedFunctions.forEach { child("nested function ").printLn(it) }
        element.nestedTypeAliases.forEach { println(it) }
        printLn("}")
    }

    private fun Env.process(element: KtFileFacade) {
        printLn("file facade {")
        element.nestedProperties.forEach { child("nested property ").printLn(it) }
        element.nestedFunctions.forEach { child("nested function ").printLn(it) }
        element.nestedTypeAliases.forEach { println(it) }
        printLn("}")
    }

    private fun Env.process(element: KtClass) {
        printLn("${element.name} ${element.flags} {")
        element.versionRequirement?.let { println("versionRequirement=$it") }
        println("typeParameters=${element.typeParameters}")
        println("superTypes=${element.superTypes}")
        element.enumEntries.let { println("enumEntries=$it") }
        element.extensions.forEach { println(it) }
        element.constructors.forEach { println(it) }
        element.nestedTypeAliases.forEach { println(it) }
        element.companion?.let { child("companion object ").process(it) }
        element.nestedClasses.forEach { child("nested class ").process(it) }
        element.sealedSubclasses.forEach { child("sealed subclass ").process(it) }
        element.nestedProperties.forEach { child("nested property ").printLn(it) }
        element.nestedFunctions.forEach { child("nested function ").printLn(it) }
        printLn("}")
    }
}