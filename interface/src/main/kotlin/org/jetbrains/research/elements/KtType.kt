package org.jetbrains.research.elements

import kotlinx.metadata.ClassName
import kotlinx.metadata.KmAnnotation
import org.jetbrains.research.flags.KtDeclarationFlags

interface KtType : KtElement, KtWithFlags<KtDeclarationFlags> {

    /**
     * ToDo kotlin comment
     **/
    val extensions: List<KtExtension>

    /**
     * ToDo kotlin comment
     **/
    val abbreviatedType: KtType?

    /**
     * ToDo kotlin comment
     **/
    val outerType: KtType?

    /**
     * ToDo kotlin comment
     **/
    val typeFlexibilityId: String?

    /**
     * ToDo kotlin comment
     **/
    val flexibleUpperBound: KtType?

    /**
     * ToDo kotlin comment
     **/
    val typeArguments: List<KtTypeArgument>

    /**
     * ToDo kotlin comment
     **/
    val origin: Origin

    sealed class Origin {

        /**
         * ToDo kotlin comment
         **/
        data class TypeParameter(val id: Int) : Origin()

        /**
         * ToDo kotlin comment
         **/
        data class Class(val name: ClassName) : Origin()

        /**
         * ToDo kotlin comment
         **/
        data class TypeAlias(val name: ClassName) : Origin()
    }

    interface KtExtension {

        /**
         * ToDo kotlin comment
         **/
        val isRaw: Boolean?

        /**
         * ToDo kotlin comment
         **/
        val annotations: List<KmAnnotation>
    }
}
