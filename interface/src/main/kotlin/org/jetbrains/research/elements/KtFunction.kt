package org.jetbrains.research.elements

import org.jetbrains.research.flags.KtFunctionsFlags
import javax.lang.model.element.Element

interface KtFunction : KtElement, KtWithFlags<KtFunctionsFlags> {

    /**
     * ToDo kotlin comment
     **/
    override val getParent: () -> KtElement

    /**
     * ToDo kotlin comment
     **/
    override val javaElement: Element?

    /**
     * ToDo kotlin comment
     **/
    val name: String

    /**
     * ToDo kotlin comment
     **/
    val contract: KtContract?

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
    val typeParameters: List<KtTypeParameter>

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

        /**
         * ToDo kotlin comment
         **/
        val lambdaClassOriginName: String?
    }
}
