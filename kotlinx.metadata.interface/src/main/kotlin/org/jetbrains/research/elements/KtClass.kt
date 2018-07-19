package org.jetbrains.research.elements

import kotlinx.metadata.ClassName
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.flags.KtClassFlags

interface KtClass : KtClassElement<KotlinClassMetadata.Class>, KtDeclarationContainer, KtWithFlags<KtClassFlags>, KtLazyElement {

    /**
     * ToDo kotlin comment
     **/
    val name: ClassName

    /**
     * ToDo kotlin comment
     **/
    val companion: KtClass?

    /**
     * ToDo kotlin comment
     **/
    val primaryConstructor: KtConstructor?

    /**
     * ToDo kotlin comment
     **/
    val constructors: List<KtConstructor>

    /**
     * ToDo kotlin comment
     **/
    val extensions: List<KtExtension>

    /**
     * ToDo kotlin comment
     **/
    val enumEntries: List<String>

    /**
     * ToDo kotlin comment
     **/
    val typeParameters: List<KtTypeParameter>

    /**
     * ToDo kotlin comment
     **/
    val classes: List<KtClass>

    /**
     * ToDo kotlin comment
     **/
    val superTypes: List<KtType>

    /**
     * ToDo kotlin comment
     **/
    val sealedSubclasses: List<KtClass>

    /**
     * ToDo kotlin comment
     **/
    val versionRequirement: KtVersionRequirement?

    interface KtExtension {

        /**
         * ToDo kotlin comment
         **/
        val anonymousObjectOriginName: String?
    }
}
