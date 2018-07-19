package org.jetbrains.research.flags

import kotlinx.metadata.Flags

abstract class KtFlags(val flags: Flags) {
    abstract val names: List<String>

    override fun toString() = "($flags)$names"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KtFlags) return false
        if (flags != other.flags) return false
        return true
    }

    override fun hashCode(): Int {
        return flags
    }
}