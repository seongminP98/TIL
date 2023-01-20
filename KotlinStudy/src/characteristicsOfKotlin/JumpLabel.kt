package characteristicsOfKotlin

fun main() {

    val numbers = listOf(1, 2, 3)
    numbers.map { it + 1 }
        .forEach { println(it) }

    // forEach 에서는 continue, break 사용 불가능

    run {
        numbers.forEach {
            if (it == 2) {
                return@run
            }
            println(it)
        }
    }

    // Label 사용
    abc@ for (i in 1..100) {
        for (j in 1..100) {
            if (j == 2) {
                break@abc
            }
            println("$i $j")
        }
    }
}