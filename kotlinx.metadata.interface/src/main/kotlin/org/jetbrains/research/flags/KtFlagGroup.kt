package org.jetbrains.research.flags

open class KtFlagGroup<E : Enum<*>>(private val group: E) {
    override fun toString() = group.name
}