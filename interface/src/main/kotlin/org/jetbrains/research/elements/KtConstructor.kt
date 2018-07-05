package org.jetbrains.research.elements

import org.jetbrains.research.flags.KtConstructorsFlags

interface KtConstructor : KtElement, KtWithFlags<KtConstructorsFlags> {

    /**
     * ToDo kotlin comment
     **/
    val extensions: List<KtExtension>

    /**
     * ToDo kotlin comment
     **/
    val valueParameters: List<KtValueParameter>

    /**
     * ToDo kotlin comment
     **/
    val versionRequirement: KtVersionRequirement?

    interface KtExtension {

        /**
         * ToDo kotlin comment
         **/
        val descriptor: String?
    }
}
