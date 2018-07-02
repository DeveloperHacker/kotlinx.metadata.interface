package org.jetbrains.research.elements

import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.environments.KtEnvironment
import javax.lang.model.element.Element

class KtMultiFileClassFacade(environment: KtEnvironment, javaElement: Element, metadata: KotlinClassMetadata.MultiFileClassFacade) :
    KtClassElement<KotlinClassMetadata.MultiFileClassFacade>(environment, javaElement, metadata)
