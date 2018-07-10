package org.jetbrains.research.elements

interface KtLazyElement : KtElement {
    fun forceInit()
}