package org.jetbrains.research.elements

import kotlinx.metadata.KmVersionRequirementLevel
import kotlinx.metadata.KmVersionRequirementVersionKind

interface KtVersionRequirement {

    /**
     * ToDo kotlin comment
     **/
    val kind: KmVersionRequirementVersionKind

    /**
     * ToDo kotlin comment
     **/
    val level: KmVersionRequirementLevel

    /**
     * ToDo kotlin comment
     **/
    val errorCode: Int?

    /**
     * ToDo kotlin comment
     **/
    val message: String?

    /**
     * ToDo kotlin comment
     **/
    val version: KtVersion

    interface KtVersion {

        /**
         * ToDo kotlin comment
         **/
        val major: Int

        /**
         * ToDo kotlin comment
         **/
        val minor: Int

        /**
         * ToDo kotlin comment
         **/
        val patch: Int
    }
}
