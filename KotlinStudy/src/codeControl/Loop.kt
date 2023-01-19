package codeControl

fun main() {

    val numbers = listOf(1L, 2L, 3L)
    for (number in numbers) {
        println(number)
    }

    for (i in 1..3) { // 1부터 3까지
        println(i)
    }

    for (i in 3 downTo 1) { // 아래로
        println(i)
    }

    for (i in 1..5 step 2) { // 2씩 증가
        println(i)
    }
}