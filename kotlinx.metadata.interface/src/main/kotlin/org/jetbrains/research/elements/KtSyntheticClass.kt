package org.jetbrains.research.elements

import kotlinx.metadata.jvm.KotlinClassMetadata

interface KtSyntheticClass : KtClassElement<KotlinClassMetadata.SyntheticClass>, KtLazyElement {

    /**
     * ToDo kotlin comment
     **/
    val function: KtFunction
}
