package org.jetbrains.research

import compile.toPrettyString

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

fun main(args: Array<String>) {
    println(Tuple4(1, 2, 3, 4).toPrettyString())
}
