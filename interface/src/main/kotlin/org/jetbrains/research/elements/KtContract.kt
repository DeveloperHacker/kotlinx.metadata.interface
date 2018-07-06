package org.jetbrains.research.elements

interface KtContract : KtElement {

    /**
     * ToDo kotlin comment
     **/
    override val getParent: () -> KtFunction

    /**
     * ToDo kotlin comment
     **/
    val effects: List<KtEffect>
}
