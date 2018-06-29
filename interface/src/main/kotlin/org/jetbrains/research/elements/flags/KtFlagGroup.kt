package org.jetbrains.research.elements.flags

open class KtFlagGroup<E : Enum<*>>(private val group: E) {
    override fun toString() = group.name
}