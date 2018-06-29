package org.jetbrains.research.elements.flags

import org.jetbrains.kotlin.metadata.ProtoBuf

class KtClassKind(classKind: ProtoBuf.Class.Kind) : KtFlagGroup<ProtoBuf.Class.Kind>(classKind) {
    val isClass = classKind == ProtoBuf.Class.Kind.CLASS
    val isInterface = classKind == ProtoBuf.Class.Kind.INTERFACE
    val isEnumClass = classKind == ProtoBuf.Class.Kind.ENUM_CLASS
    val isEnumEntry = classKind == ProtoBuf.Class.Kind.ENUM_ENTRY
    val isAnnotationClass = classKind == ProtoBuf.Class.Kind.ANNOTATION_CLASS
    val isObject = classKind == ProtoBuf.Class.Kind.OBJECT
    val isCompanionObject = classKind == ProtoBuf.Class.Kind.COMPANION_OBJECT
}