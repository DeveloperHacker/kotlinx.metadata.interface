package org.jetbrains.research.elements

import kotlinx.metadata.jvm.KotlinClassMetadata

interface KtSyntheticClass : KtClassElement<KotlinClassMetadata.SyntheticClass> {

    /**
     * ToDo kotlin comment
     **/
    val function: KtFunction
}
