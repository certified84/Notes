package com.certified.notes

fun main() {
    val list = listOf("Kotlin", "Java", "Python", "C++")
    val map = mapOf("K" to list[0], "J" to list[1], "P" to list[2], 3 to list[3])
    map.forEach { _, value -> println(value) }
}