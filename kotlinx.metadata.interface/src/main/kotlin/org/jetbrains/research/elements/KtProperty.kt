package org.jetbrains.research.elements

import org.jetbrains.research.flags.KtFunctionsFlags

interface KtProperty : KtElement, KtWithFlags<org.jetbrains.research.flags.KtPropertiesFlags> {

    /**
     * ToDo kotlin comment
     **/
    override val getParent: () -> KtElement

    /**
     * ToDo kotlin comment
     **/
    val name: String

    /**
     * ToDo kotlin comment
     **/
    val getterFlags: KtFunctionsFlags

    /**
     * ToDo kotlin comment
     **/
    val setterFlags: KtFunctionsFlags

    /**
     * ToDo kotlin comment
     **/
    val extensions: List<KtExtension>

    /**
     * ToDo kotlin comment
     **/
    val receiverParameterType: KtType?

    /**
     * ToDo kotlin comment
     **/
    val returnType: KtType

    /**
     * ToDo kotlin comment
     **/
    val setterValueParameter: KtValueParameter?

    /**
     * ToDo kotlin comment
     **/
    val typeParameters: List<KtTypeParameter>

    /**
     * ToDo kotlin comment
     **/
    val versionRequirement: KtVersionRequirement?

    interface KtExtension {

        /**
         * ToDo kotlin comment
         **/
        val fieldName: String?

        /**
         * ToDo kotlin comment
         **/
        val fieldTypeDesc: String?

        /**
         * ToDo kotlin comment
         **/
        val getterDesc: String?

        /**
         * ToDo kotlin comment
         **/
        val setterDesc: String?

        /**
         * ToDo kotlin comment
         **/
        val syntheticMethodForAnnotations: String?
    }
}
