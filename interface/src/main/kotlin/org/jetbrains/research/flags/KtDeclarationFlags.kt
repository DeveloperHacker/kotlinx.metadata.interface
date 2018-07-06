package org.jetbrains.research.flags

import kotlinx.metadata.Flags
import org.jetbrains.kotlin.metadata.deserialization.Flags.*
import java.util.*


open class KtDeclarationFlags(flags: Flags) : KtFlags(flags) {
    val hasAnnotations: Boolean by lazy { HAS_ANNOTATIONS.get(flags) }
    val visibility by lazy { KtVisibility(VISIBILITY.get(flags)!!) }
    val modality by lazy { KtModality(MODALITY.get(flags)!!) }

    override val names: List<String> by lazy {
        val result = ArrayList<String>()
        if (hasAnnotations) result.add("HAS_ANNOTATIONS")
        result.add(visibility.toString())
        result.add(modality.toString())
        result
    }
}