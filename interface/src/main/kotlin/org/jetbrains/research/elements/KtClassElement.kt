package org.jetbrains.research.elements

import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.environments.KtEnvironment
import org.jetbrains.research.qualifiedIdentificator
import javax.lang.model.element.Element

abstract class KtClassElement<T : KotlinClassMetadata>(
    val environment: KtEnvironment,
    val javaElement: Element,
    val metadata: T
) {
    val simpleName = javaElement.simpleName.toString()
    val qualifiedIdentificator = javaElement.qualifiedIdentificator
    val kind = javaElement.kind!!
}
