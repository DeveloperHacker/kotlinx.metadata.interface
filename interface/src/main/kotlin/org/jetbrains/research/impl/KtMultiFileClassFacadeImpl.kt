package org.jetbrains.research.impl

import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.elements.KtMultiFileClassFacade
import javax.lang.model.element.Element

class KtMultiFileClassFacadeImpl(
    override val javaElement: Element,
    override val metadata: KotlinClassMetadata.MultiFileClassFacade
) : KtMultiFileClassFacade
