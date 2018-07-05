package org.jetbrains.research.flags

import org.jetbrains.kotlin.metadata.ProtoBuf

class KtMemberKind(memberKind: ProtoBuf.MemberKind) : KtFlagGroup<ProtoBuf.MemberKind>(memberKind) {
    val isDeclaration = memberKind == ProtoBuf.MemberKind.DECLARATION
    val isFakeOverride = memberKind == ProtoBuf.MemberKind.FAKE_OVERRIDE
    val isDelegation = memberKind == ProtoBuf.MemberKind.DELEGATION
    val isSynthesized = memberKind == ProtoBuf.MemberKind.SYNTHESIZED
}