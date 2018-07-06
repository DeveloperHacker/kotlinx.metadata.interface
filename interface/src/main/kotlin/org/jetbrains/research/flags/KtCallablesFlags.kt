package org.jetbrains.research.flags

import kotlinx.metadata.Flags
import org.jetbrains.kotlin.metadata.deserialization.Flags.MEMBER_KIND
import java.util.*

open class KtCallablesFlags(flags: Flags) : KtDeclarationFlags(flags) {
    val memberKind by lazy { KtMemberKind(MEMBER_KIND.get(flags)!!) }

    override val names by lazy {
        val result = ArrayList<String>()
        result.add(memberKind.toString())
        super.names + result
    }
}