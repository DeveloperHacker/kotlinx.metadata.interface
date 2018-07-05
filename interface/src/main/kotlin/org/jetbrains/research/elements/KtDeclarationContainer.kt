package org.jetbrains.research.elements

interface KtDeclarationContainer {

    /**
     * ToDo kotlin comment
     **/
    val functions: List<KtFunction>

    /**
     * ToDo kotlin comment
     **/
    val properties: List<KtProperty>

    /**
     * ToDo kotlin comment
     **/
    val typeAliases: List<KtTypeAlias>
}
