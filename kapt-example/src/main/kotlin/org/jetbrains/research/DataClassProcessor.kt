package org.jetbrains.research

import com.google.auto.service.AutoService
import org.jetbrains.research.elements.KtClass
import org.jetbrains.research.elements.KtEnvironment
import org.jetbrains.research.elements.publicProperties
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.jetbrains.research.DataClass")
@AutoService(Processor::class)
class DataClassProcessor : KtAbstractProcessor<DataClass>(DataClass::class.java) {
    override fun process(kaptKotlinGeneratedDir: String, environment: KtEnvironment) {
        val classes = environment
            .getKtElements(DataClass::class.java)
            .filterIsInstance<KtClass>()
        val ktFile = StringBuilder()
        ktFile.append("package compile\n\n\n")
        for (ktClass in classes) {
            ktFile.appendGeneratedEquals(ktClass)
            ktFile.append("\n")
            ktFile.appendGeneratedHashCode(ktClass)
            ktFile.append("\n")
            ktFile.appendGeneratedToString(ktClass)
            ktFile.append("\n")
        }
        File(kaptKotlinGeneratedDir, "Data.kt")
            .apply { parentFile.mkdirs() }
            .apply { createNewFile() }
            .writeText(ktFile.toString())
    }

    private val KtClass.type
        get(): String {
            val receiver = name.replace('/', '.')
            val typeParameters = typeParameters.joinToString(", ") { "*" }
            val typeParametersStr = if (typeParameters.isNotEmpty()) "<$typeParameters>" else typeParameters
            return receiver + typeParametersStr
        }


    private fun StringBuilder.appendGeneratedEquals(ktClass: KtClass) {
        val clazz = ktClass.type
        append("fun $clazz.generatedEquals(other: Any?): Boolean {\n")
        append("    if (this === other) return true\n")
        append("    if (other !is $clazz) return false\n")
        for (property in ktClass.publicProperties)
            append("    if (${property.name} != other.${property.name}) return false\n")
        append("    return true\n")
        append("}\n")
    }

    private fun StringBuilder.appendGeneratedHashCode(ktClass: KtClass) {
        val properties = ktClass.publicProperties
        if (properties.isEmpty()) {
            append("fun ${ktClass.type}.generatedHashCode() = javaClass.hashCode()\n")
            return
        }
        val firstProperty = properties.first()
        if (properties.size == 1) {
            append("fun ${ktClass.type}.generatedHashCode() = ${firstProperty.name}.hashCode()\n")
            return
        }
        val otherProperties = properties.drop(1)
        append("fun ${ktClass.type}.generatedHashCode(): Int{\n")
        append("    var result = ${firstProperty.name}.hashCode()\n")
        for (property in otherProperties)
            append("    result = 31 * result + ${property.name}.hashCode() + 7\n")
        append("    return result\n")
        append("}\n")
    }

    private fun StringBuilder.appendGeneratedToString(ktClass: KtClass) {
        val name = ktClass.javaElement.simpleName.toString()
        val parameterStr = ktClass.publicProperties.joinToString(",") { "${it.name}=$${it.name}" }
        append("fun ${ktClass.type}.generatedToString() = \"$name($parameterStr)\"\n")
    }
}
