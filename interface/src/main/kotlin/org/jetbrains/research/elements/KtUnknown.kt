package org.jetbrains.research.elements

import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.environments.KtEnvironment
import javax.lang.model.element.Element

class KtUnknown(environment: KtEnvironment, javaElement: Element, metadata: KotlinClassMetadata.Unknown) :
    KtClassElement<KotlinClassMetadata.Unknown>(environment, javaElement, metadata)
