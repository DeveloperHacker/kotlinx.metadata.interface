package org.jetbrains.research

import com.google.auto.service.AutoService
import org.jetbrains.research.elements.KtEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion


@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.jetbrains.research.Debug")
@AutoService(Processor::class)
class DebugProcessor : KtAbstractProcessor<Debug>(Debug::class.java) {
    override fun process(kaptKotlinGeneratedDir: String, environment: KtEnvironment) {
//        val ktClass = environment.getKtElements(Debug::class.java).filterIsInstance<KtClass>().first()
//        for (constructor in ktClass.constructors)
//            println("${constructor.descriptor} ${constructor.javaElement}")
//        println("-------------------------------------------------------------")
//        for (constructor in ktClass.javaElement.enclosedElements.filter { it.kind == ElementKind.CONSTRUCTOR })
//            println("$constructor ${IdentityFunction.valueOf(constructor)}")
    }
}
