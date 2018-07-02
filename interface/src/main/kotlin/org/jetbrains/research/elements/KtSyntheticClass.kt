package org.jetbrains.research.elements

import kotlinx.metadata.Flags
import kotlinx.metadata.KmLambdaVisitor
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.LazyInitializer
import org.jetbrains.research.environments.KtEnvironment
import javax.lang.model.element.Element

class KtSyntheticClass(environment: KtEnvironment, javaElement: Element, metadata: KotlinClassMetadata.SyntheticClass) :
    KtClassElement<KotlinClassMetadata.SyntheticClass>(environment, javaElement, metadata) {

    private val lazyInitializer = LazyInitializer {
        metadata.accept(object : KmLambdaVisitor() {
            lateinit var function_: KtFunction

            override fun visitFunction(flags: Flags, name: String) = KtFunction(environment, flags, name) {
                function_ = it
            }

            override fun visitEnd() {
                function = function_
            }
        })
    }

    var function: KtFunction by lazyInitializer.Property()
        private set
}