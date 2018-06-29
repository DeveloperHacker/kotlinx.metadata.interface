package org.jetbrains.research.elements.flags

import kotlinx.metadata.Flags
import org.jetbrains.kotlin.metadata.deserialization.Flags.MEMBER_KIND
import java.util.*

open class KtCallablesFlags(flags: Flags) : KtDeclarationFlags(flags) {
    val memberKind by lazy { MEMBER_KIND.get(flags)?.let { KtMemberKind(it) } }

    override val names by lazy {
        val result = ArrayList<String>()
        memberKind?.let { result.add(it.toString()) }
        super.names + result
    }
}