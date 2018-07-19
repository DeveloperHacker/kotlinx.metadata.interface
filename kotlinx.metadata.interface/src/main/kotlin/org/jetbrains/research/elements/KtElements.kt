package org.jetbrains.research.elements

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

val KtDeclarationContainer.publicProperties: List<KtProperty>
    get() = properties.filter { it.flags.visibility.isPublic }

val Element.qualifiedIdentificator: String
    get() = when (kind) {
        ElementKind.PACKAGE, ElementKind.CLASS, ElementKind.ANNOTATION_TYPE -> "[$kind][$this]"
        else -> "[$kind][${enclosingElement?.qualifiedIdentificator}][$this]"
    }

val KtElement.qualifiedIdentificator: String?
    get() = javaElement?.qualifiedIdentificator

val KtFunction.descriptor: String?
    get(): String? {
        var result: String? = null
        for (extension in extensions) {
            val descriptor = extension.descriptor ?: continue
            if (result != null && descriptor != result)
                error("Incompatible function descriptors $result and $descriptor")
            result = descriptor
        }
        return result
    }

val KtConstructor.descriptor: String?
    get(): String? {
        var result: String? = null
        for (extension in extensions) {
            val descriptor = extension.descriptor ?: continue
            if (result != null && descriptor != result)
                error("Incompatible function descriptors $result and $descriptor")
            result = descriptor
        }
        return result
    }

val KtElement?.allTypeParameters: List<KtTypeParameter>
    get() = when (this) {
        is KtClass -> typeParameters + getParent().allTypeParameters
        is KtFunction -> typeParameters + getParent().allTypeParameters
        is KtConstructor -> getParent().allTypeParameters
        is KtProperty -> typeParameters + getParent().allTypeParameters
        else -> emptyList()
    }

val KtClass.simpleName: String
    get() = javaElement.simpleName.toString()

fun <T : Annotation> KtElement.getAnnotation(annotationType: Class<T>) = javaElement?.getAnnotation(annotationType)
