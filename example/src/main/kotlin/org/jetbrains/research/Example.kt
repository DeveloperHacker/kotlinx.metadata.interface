package org.jetbrains.research

import kotlin.reflect.jvm.reflect

@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPE,
    AnnotationTarget.EXPRESSION,
    AnnotationTarget.FILE,
    AnnotationTarget.TYPEALIAS
)
annotation class OnAnyThing(val x: Int = 5)

data class OhMy(val i: Int)

data class ReallyThere(val i: Double?, @OnAnyThing val s: @OnAnyThing String, val k: OhMy)

object Obj

sealed class Expr

data class Var(val name: String)

@PrettyPrintable
data class Const(val value: Int)

class AAA(val a: Int) {
    constructor(b: Int, c: Int, d: Int): this(b)
    constructor(b: Int, c: Int, d: Int, e: Int): this(b)
    constructor(b: Int, c: Int, d: String): this(b)
    constructor(b: Int, c: Int): this(b)
    constructor(b: Int, c: Int, d: Int, e: String): this(b)
    constructor(b: Int, c: String): this(b)
}

class FullBlown {
    inner class Inner

    class NotInner

    companion object {
        class AlsoAClass
    }

    enum class A { A, B, C; }
}

enum class EnumExample { BADGER, RED, NINE, NIEN, ROOSEVELT; }

val x = { x: Int -> x + 1 }
fun code() {
    val x = x.reflect()
    x?.call(x)
}
