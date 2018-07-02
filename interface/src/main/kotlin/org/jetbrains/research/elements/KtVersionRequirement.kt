package org.jetbrains.research.elements

import kotlinx.metadata.KmVersionRequirementLevel
import kotlinx.metadata.KmVersionRequirementVersionKind
import kotlinx.metadata.KmVersionRequirementVisitor
import org.jetbrains.research.KtWrapper

data class KtVersionRequirement(
    val kind: KmVersionRequirementVersionKind,
    val level: KmVersionRequirementLevel,
    val errorCode: Int?,
    val message: String?,
    val version: KtVersion
) {

    data class KtVersion(val major: Int, val minor: Int, val patch: Int)

    companion object {
        operator fun invoke(resultListener: (KtVersionRequirement) -> Unit) = object : KmVersionRequirementVisitor() {
            lateinit var kind: KmVersionRequirementVersionKind
            lateinit var level: KmVersionRequirementLevel
            lateinit var errorCode: KtWrapper<Int?>
            lateinit var message: KtWrapper<String?>
            lateinit var version: KtVersion

            override fun visit(kind: KmVersionRequirementVersionKind, level: KmVersionRequirementLevel, errorCode: Int?, message: String?) {
                this.kind = kind
                this.level = level
                this.errorCode = KtWrapper(errorCode)
                this.message = KtWrapper(message)
            }

            override fun visitEnd() {
                val versionRequirement = KtVersionRequirement(kind, level, errorCode.value, message.value, version)
                resultListener(versionRequirement)
            }

            override fun visitVersion(major: Int, minor: Int, patch: Int) {
                version = KtVersion(major, minor, patch)
            }
        }
    }
}