package org.jetbrains.research.impl

import org.jetbrains.research.elements.*
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

data class IdentityFunction(val name: String, val valueParameters: List<String>) {

    private data class JvmFunction(val name: String, val valueParameters: List<String>, val returnType: String)

    private data class JavaFunction(val typeParameters: List<String>, val name: String, val valueParameters: List<String>)

    companion object {
        private val primitiveTypes = mapOf(
            "B" to "byte",
            "C" to "char",
            "D" to "double",
            "F" to "float",
            "I" to "int",
            "J" to "long",
            "S" to "short",
            "Z" to "boolean"
        )

        fun valueOf(constructor: KtConstructor): IdentityFunction? {
            val valueParameters = constructor.valueParameters.map { it.type.origin }
            val typeParameters = constructor.allTypeParameters.map { it.id to it.name }.toMap()
            val descriptor = constructor.descriptor ?: return null
            val jvmMethod = parseJvmFunction(descriptor)
            val ktClass = constructor.getParent()
            val function = IdentityFunction.valueOf(jvmMethod, valueParameters, typeParameters)
            return IdentityFunction(ktClass.simpleName, function.valueParameters)
        }

        fun valueOf(function: KtFunction): IdentityFunction? {
            val receiver = function.receiverParameterType?.origin
            val valueParameters = (listOf(receiver) + function.valueParameters.map { it.type.origin }).filterNotNull()
            val typeParameters = function.allTypeParameters.map { it.id to it.name }.toMap()
            val descriptor = function.descriptor ?: return null
            val jvmMethod = parseJvmFunction(descriptor)
            return IdentityFunction.valueOf(jvmMethod, valueParameters, typeParameters)
        }

        fun valueOf(function: Element): IdentityFunction? {
            if (function.kind !in setOf(ElementKind.METHOD, ElementKind.CONSTRUCTOR)) return null
            val signature = function.toString()
            val javaMethod = parseJavaFunction(signature)
            return IdentityFunction.valueOf(javaMethod)
        }

        private fun valueOf(
            jvmFunction: JvmFunction,
            kotlinValueParameters: List<KtType.Origin>,
            typeParameters: Map<Int, String>
        ): IdentityFunction {
            val jvmValueParameters = jvmFunction.valueParameters
            val valueParameters = ArrayList<String>()
            if (jvmValueParameters.size != kotlinValueParameters.size)
                error("IdentityFunction.valueOf #1 $jvmValueParameters $kotlinValueParameters")
            for ((jvm, kotlin) in jvmValueParameters.zip(kotlinValueParameters)) {
                if (kotlin is KtType.Origin.TypeParameter) {
                    val name = typeParameters[kotlin.id] ?: error("IdentityFunction.valueOf #2 ${kotlin.id}")
                    valueParameters.add(name)
                    continue
                }
                val arrayDimensions = jvm.count { it == '[' }
                val arrayPostfix = "[]".repeat(arrayDimensions)
                val jvmName = jvm.drop(arrayDimensions)
                val name = when {
                    jvmName in primitiveTypes -> primitiveTypes[jvmName]!!
                    jvmName.startsWith('L') && jvmName.last() == ';' -> jvmName.drop(1).dropLast(1).replace('/', '.').replace('$', '.')
                    else -> error("IdentityFunction.valueOf #3 $jvmName")
                }
                valueParameters.add(name + arrayPostfix)
            }
            return IdentityFunction(jvmFunction.name, valueParameters)
        }

        private fun valueOf(javaFunction: JavaFunction): IdentityFunction {
            val valueParameters = javaFunction.valueParameters.map { it.replace("...", "[]") }
            return IdentityFunction(javaFunction.name, valueParameters)
        }

        private fun parseJvmFunction(signature: String): JvmFunction {
            val regex = "([^(]+)\\(([^(]*)\\)([^(]+)".toRegex()
            val match = regex.matchEntire(signature) ?: error("JvmFunction.parseJvmFunction #1")
            val name = match.groups[1]?.value ?: error("JvmFunction.parseJvmFunction #2")
            val valueParametersStr = match.groups[2]?.value ?: error("JvmFunction.parseJvmFunction #3")
            val returnType = match.groups[3]?.value ?: error("JvmFunction.parseJvmFunction #4")
            val valueParameters = ArrayList<String>()
            var current: StringBuilder? = null
            var array = StringBuilder()
            for (ch in valueParametersStr) {
                if (ch == '[') {
                    if (current != null) error("JvmFunction.parseJvmFunction #5")
                    array.append(ch)
                    continue
                }
                if (ch == 'L' && current == null) {
                    current = StringBuilder()
                }
                if (current != null) {
                    current.append(ch)
                } else {
                    valueParameters.add(array.append(ch).toString())
                    array = StringBuilder()
                }
                if (ch == ';') {
                    if (current == null) error("JvmFunction.parseJvmFunction #6")
                    valueParameters.add(array.append(current).toString())
                    array = StringBuilder()
                    current = null
                }
            }
            if (array.isNotEmpty()) error("JvmFunction.parseJvmFunction #7")
            if (current != null) error("JvmFunction.parseJvmFunction #8")
            return JvmFunction(name, valueParameters, returnType)
        }

        private fun parseJavaFunction(signature: String): JavaFunction {
            val regex = "(<([^>]+)>)?([^(]+)\\(([^(]*)\\)".toRegex()
            val match = regex.matchEntire(signature) ?: error("JavaFunction.parseJvmFunction #1")
            val typeParameters = match.groups[2]?.value?.split(',') ?: listOf()
            val name = match.groups[3]?.value ?: error("JavaFunction.parseJvmFunction #2")
            val draftValueParametersStr = match.groups[4]?.value ?: error("JavaFunction.parseJvmFunction #3")
            val valueParametersStr = StringBuilder()
            var genericDepth = 0
            for (ch in draftValueParametersStr) {
                when {
                    ch == '<' -> ++genericDepth
                    ch == '>' -> --genericDepth
                    genericDepth == 0 -> valueParametersStr.append(ch)
                }
            }
            if (genericDepth != 0) error("JavaFunction.parseJvmFunction #4")
            val valueParameters = valueParametersStr.toString().split(',')
            return JavaFunction(typeParameters, name, valueParameters)
        }
    }
}