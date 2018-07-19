package org.jetbrains.research.impl

import kotlinx.metadata.KmVersionRequirementLevel
import kotlinx.metadata.KmVersionRequirementVersionKind
import kotlinx.metadata.KmVersionRequirementVisitor
import org.jetbrains.research.elements.KtVersionRequirement
import org.jetbrains.research.utlis.KtWrapper

data class KtVersionRequirementImpl(
    override val kind: KmVersionRequirementVersionKind,
    override val level: KmVersionRequirementLevel,
    override val errorCode: Int?,
    override val message: String?,
    override val version: KtVersionRequirement.KtVersion
) : KtVersionRequirement {

    data class KtVersionImpl(override val major: Int, override val minor: Int, override val patch: Int) : KtVersionRequirement.KtVersion

    companion object {
        operator fun invoke(resultListener: (KtVersionRequirementImpl) -> Unit) = object : KmVersionRequirementVisitor() {
            lateinit var kind: KmVersionRequirementVersionKind
            lateinit var level: KmVersionRequirementLevel
            lateinit var errorCode: KtWrapper<Int?>
            lateinit var message: KtWrapper<String?>
            lateinit var version: KtVersionRequirement.KtVersion

            override fun visit(kind: KmVersionRequirementVersionKind, level: KmVersionRequirementLevel, errorCode: Int?, message: String?) {
                this.kind = kind
                this.level = level
                this.errorCode = KtWrapper(errorCode)
                this.message = KtWrapper(message)
            }

            override fun visitEnd() {
                val versionRequirement = KtVersionRequirementImpl(kind, level, errorCode.value, message.value, version)
                resultListener(versionRequirement)
            }

            override fun visitVersion(major: Int, minor: Int, patch: Int) {
                version = KtVersionImpl(major, minor, patch)
            }
        }
    }
}
