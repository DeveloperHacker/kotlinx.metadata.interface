package org.jetbrains.research

import compile.generatedEquals
import compile.generatedHashCode
import compile.generatedToString
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

@DataClass
sealed class Expr(val value: Int) {
    override fun equals(other: Any?) = generatedEquals(other)

    override fun hashCode() = generatedHashCode()

    override fun toString() = generatedToString()
}

data class Var(val name: String)

@PrettyPrintable
data class Const(val value: Int)

@Suppress("ProtectedInFinal", "unused", "UNUSED_PARAMETER", "NOTHING_TO_INLINE")
@DataClass
@Debug
class AAA<C>(val a: Int) {

    val b = 10
    private val c = 10
    protected val d = 10
    val e = 10

    constructor(b: Int, c: Int, d: Int) : this(b)
    constructor(b: Int, c: Int, d: Int, e: Int) : this(b)
    constructor(b: Int, c: Int, d: String) : this(b)
    constructor(b: Int, c: Int) : this(b)
    constructor(b: Int, c: Int, d: Int, e: String) : this(b)
    constructor(b: Int, c: String) : this(b)

    @JvmName("moo")
    fun foo(b: Int, c: Int, d: Int) = 1

    inline fun foo(b: Int, c: Int, d: Int, e: Int) = 1
    fun foo(b: Int, c: Int, d: String) = 1
    fun foo(b: Int, c: Int) = 1
    fun foo(b: Int, c: Int, d: Int, e: String) = 1
    fun foo(b: Int, c: String) = 1
    fun <T> foo(b: T, c: C) = 1

    @JvmName("moo")
    fun <T, C> foo(b: T, c: C) = 1

    fun <T> foo(b: List<T>, c: C) = 1

    @JvmName("moo")
    fun <T> foo(b: List<List<T>>, c: C) = 1

    fun foo1(vararg b: Int, c: C) = 1
    fun foo2(vararg b: Int) = 1
    fun bar(a: Int, vararg b: Array<Int>) = 1
    fun bar(a: Int, vararg b: IntArray) = 1
    fun foo3(b: Array<Int>) = 1
    fun foo4(c: C, b: IntArray) = 1

    fun FullBlown.foo(b: Int, c: Int, d: Int) = 1
    fun FullBlown.foo(b: Int, c: Int, d: Int, e: Int) = 1
    fun Const.foo(b: Int, c: Int) = 1
    fun FullBlown.foo(b: Int, c: Int, d: String) = 1
    fun Const.foo(b: Int, c: Int, d: Int, e: String) = 1
    fun Const.foo(b: Int, c: String) = 1

    fun FullBlown.NotInner.foo(b: Int, c: String) = 1

    override fun equals(other: Any?) = generatedEquals(other)

    override fun hashCode() = generatedHashCode()

    override fun toString() = generatedToString()

}

@Suppress("ProtectedInFinal", "unused", "UNUSED_PARAMETER")
@DataClass
class BBB(val a: Int) {

    val b = 10
    private val c = 10
    protected val d = 10
    val e = 10

    override fun equals(other: Any?) = generatedEquals(other)

    override fun hashCode() = generatedHashCode()

    override fun toString() = generatedToString()
}

@DataClass
class FullBlown {
    inner class Inner

    class NotInner

    companion object {
        class AlsoAClass
    }

    enum class A { A, B, C; }

    override fun equals(other: Any?) = generatedEquals(other)

    override fun hashCode() = generatedHashCode()

    override fun toString() = generatedToString()
}

enum class EnumExample { BADGER, RED, NINE, NIEN, ROOSEVELT; }

val x = { x: Int -> x + 1 }
fun code() {
    val x = x.reflect()
    x?.call(x)
}
