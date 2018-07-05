package org.jetbrains.research.flags

import kotlinx.metadata.Flags
import org.jetbrains.kotlin.metadata.deserialization.Flags.*
import java.util.*


open class KtDeclarationFlags(flags: Flags) : KtFlags(flags) {
    val hasAnnotations: Boolean by lazy { HAS_ANNOTATIONS.get(flags) }
    val visibility by lazy { VISIBILITY.get(flags)?.let { KtVisibility(it) } }
    val modality by lazy { MODALITY.get(flags)?.let { KtModality(it) } }

    override val names: List<String> by lazy {
        val result = ArrayList<String>()
        if (hasAnnotations) result.add("HAS_ANNOTATIONS")
        visibility?.let { result.add(it.toString()) }
        modality?.let { result.add(it.toString()) }
        result
    }
}