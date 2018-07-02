package org.jetbrains.research.elements

import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.environments.KtEnvironment
import javax.lang.model.element.Element

abstract class KtClassElement<T : KotlinClassMetadata>(val environment: KtEnvironment, val javaElement: Element, val metadata: T)
