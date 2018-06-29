package org.jetbrains.research.elements.flags

import org.jetbrains.kotlin.metadata.ProtoBuf

class KtModality(modality: ProtoBuf.Modality) : KtFlagGroup<ProtoBuf.Modality>(modality) {
    val isFinal = modality == ProtoBuf.Modality.FINAL
    val isOpen = modality == ProtoBuf.Modality.OPEN
    val isAbstract = modality == ProtoBuf.Modality.ABSTRACT
    val isSealed = modality == ProtoBuf.Modality.SEALED
}