package org.jetbrains.research.impl

import kotlinx.metadata.Flags
import kotlinx.metadata.KmLambdaVisitor
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.jetbrains.research.elements.KtElement
import org.jetbrains.research.elements.KtEnvironment
import org.jetbrains.research.elements.KtFunction
import org.jetbrains.research.elements.KtSyntheticClass
import org.jetbrains.research.utlis.LazyInitializer
import javax.lang.model.element.Element

class KtSyntheticClassImpl(
    environment: KtEnvironment,
    override val javaElement: Element,
    override val metadata: KotlinClassMetadata.SyntheticClass,
    override val getParent: () -> KtElement?
) : KtSyntheticClass {

    private val lazyInitializer = LazyInitializer {
        metadata.accept(object : KmLambdaVisitor() {
            lateinit var function_: KtFunction
            val lazySelf = { this@KtSyntheticClassImpl }

            override fun visitFunction(flags: Flags, name: String) = KtFunctionImpl(environment, lazySelf, flags, name) {
                function_ = it
            }

            override fun visitEnd() {
                function = function_
            }
        })
    }

    override var function: KtFunction by lazyInitializer.Property()
        private set
}