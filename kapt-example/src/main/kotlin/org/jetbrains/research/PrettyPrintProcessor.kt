package org.jetbrains.research

import org.jetbrains.research.elements.KtClass
import org.jetbrains.research.elements.KtEnvironment
import java.io.File
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.jetbrains.research.PrettyPrintable")
//@AutoService(Processor::class)
class PrettyPrintProcessor : KtAbstractProcessor<PrettyPrintable>(PrettyPrintable::class.java) {
    override fun process(kaptKotlinGeneratedDir: String, environment: KtEnvironment) {
        val classes = environment
            .getKtElements(PrettyPrintable::class.java)
            .filterIsInstance<KtClass>()
        val ktFile = StringBuilder()
        ktFile.append("package compile\n\n\n")
        for (ktClass in classes) {
            val properties = ktClass.properties.filter { it.flags.visibility.isPublic }
            val receiver = ktClass.name.replace('/', '.')
            val typeParameters = ktClass.typeParameters.joinToString(", ") { "*" }.let { if (it.isNotEmpty()) "<$it>" else it }
            val name = ktClass.javaElement.simpleName.toString()
            val parameterStr = properties.joinToString(",\\n${'$'}{ident2}") { "${it.name} = $${it.name}" }
            val prefix = if (properties.size == 1) "" else "\\n${'$'}{ident2}"
            val postfix = if (properties.size == 1) "" else "\\n${'$'}{ident1}"
            ktFile.append("fun $receiver$typeParameters.toPrettyString(ident: Int = 0): String {\n")
            ktFile.append("    val ident1 = \" \".repeat(ident * 4)\n")
            if (properties.size != 1)
                ktFile.append("    val ident2 = \" \".repeat((ident + 1) * 4)\n")
            ktFile.append("    return \"${'$'}{ident1}$name($prefix$parameterStr$postfix)\"\n")
            ktFile.append("}\n\n")
        }
        File(kaptKotlinGeneratedDir, "Pretty.kt")
            .apply { parentFile.mkdirs() }
            .apply { createNewFile() }
            .writeText(ktFile.toString())
    }
}
