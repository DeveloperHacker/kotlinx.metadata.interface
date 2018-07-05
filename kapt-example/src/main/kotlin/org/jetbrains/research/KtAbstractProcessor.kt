package org.jetbrains.research

import org.jetbrains.research.elements.KtEnvironment
import org.jetbrains.research.impl.KtEnvironmentImpl
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedOptions(KtAbstractProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
abstract class KtAbstractProcessor<T : Annotation>(private val annotation: Class<T>) : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment): Boolean {
        val annotatedElements = roundEnvironment.getElementsAnnotatedWith(annotation)
        if (annotatedElements.isEmpty()) return false
        Thread.currentThread().contextClassLoader = javaClass.classLoader
        val kaptKotlinGeneratedDir = processingEnv.options[KtAbstractProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can't find the target directory for generated Kotlin files.")
            return false
        }
        val environment = KtEnvironmentImpl(roundEnvironment)
        process(kaptKotlinGeneratedDir, environment)
        return true
    }

    abstract fun process(kaptKotlinGeneratedDir: String, environment: KtEnvironment)
}
