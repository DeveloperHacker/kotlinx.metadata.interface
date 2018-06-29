package org.jetbrains.research.elements.flags

import org.jetbrains.kotlin.metadata.ProtoBuf

class KtVisibility(visibility: ProtoBuf.Visibility) : KtFlagGroup<ProtoBuf.Visibility>(visibility) {
    val isInternal = visibility == ProtoBuf.Visibility.INTERNAL
    val isProtected = visibility == ProtoBuf.Visibility.PROTECTED
    val isPublic = visibility == ProtoBuf.Visibility.PUBLIC
    val isPrivateToThis = visibility == ProtoBuf.Visibility.PRIVATE_TO_THIS
    val isLocal = visibility == ProtoBuf.Visibility.LOCAL
}