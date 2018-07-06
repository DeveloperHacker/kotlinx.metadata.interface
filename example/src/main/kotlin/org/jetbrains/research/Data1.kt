package org.jetbrains.research

import compile.toPrettyString
import compile.value

@PrettyPrintable
data class Tuple1<T>(val first: T) {
    @PrettyPrintable
    data class T1(val a: Int)

    override fun toString(): String = toPrettyString()
}

@PrettyPrintable
data class Tuple2(val first: Int, val second: Int)

@PrettyPrintable
data class Tuple3(val first: Int, val second: Int, val third: Int)

@PrettyPrintable
data class Tuple4(val first: Int, val second: Int, val third: Int, val fourth: Int)

class A<T>(private val a: T) {
    @Getter("value")
    fun foo() = a
}

fun main(args: Array<String>) {
    A(10).value
    println(Tuple4(1, 2, 3, 4).toPrettyString())
}
