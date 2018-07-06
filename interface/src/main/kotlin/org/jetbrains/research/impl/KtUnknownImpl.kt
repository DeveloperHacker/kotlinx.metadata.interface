package org.jetbrains.research.impl

import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.elements.KtElement
import org.jetbrains.research.elements.KtUnknown
import javax.lang.model.element.Element

class KtUnknownImpl(
    override val javaElement: Element,
    override val metadata: KotlinClassMetadata.Unknown,
    override val getParent: () -> KtElement?
) : KtUnknown
