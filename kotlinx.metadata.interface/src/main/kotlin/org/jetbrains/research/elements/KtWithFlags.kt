package org.jetbrains.research.elements

import org.jetbrains.research.flags.KtFlags

interface KtWithFlags<T : KtFlags> {

    /**
     * ToDo kotlin comment
     **/
    val flags: T
}
