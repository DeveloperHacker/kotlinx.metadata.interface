package org.jetbrains.research.elements

import kotlinx.metadata.KmAnnotation
import org.jetbrains.research.flags.KtDeclarationFlags

interface KtTypeAlias : KtElement, KtWithFlags<KtDeclarationFlags> {

    /**
     * ToDo kotlin comment
     **/
    val name: String

    /**
     * ToDo kotlin comment
     **/
    val annotations: List<KmAnnotation>

    /**
     * ToDo kotlin comment
     **/
    val expandedType: KtType

    /**
     * ToDo kotlin comment
     **/
    val underlyingType: KtType

    /**
     * ToDo kotlin comment
     **/
    val typeParameters: List<KtTypeParameter>

    /**
     * ToDo kotlin comment
     **/
    val versionRequirement: KtVersionRequirement?
}
