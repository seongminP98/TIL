package fp

fun main() {

    val numbers = listOf(100, 200) // 불변 리스트

    val numbers2 = mutableListOf(100, 200) // 가변 리스트

    val emptyList = emptyList<Int>() // 빈 리스트. 타입 명시. 타입 추론 가능하다면 생략 가능

    println(numbers[0])

    for (number in numbers) {
        println(number)
    }

    for ((idx, value) in numbers.withIndex()) {
        println("$idx $value")
    }


    val oldMap = mutableMapOf<Int, String>()
    oldMap.put(1, "MONDAY")
    oldMap[2] = "TUESDAY" // 이렇게 사용가능

    mapOf(1 to "MONDAY", 2 to "TUESDAY")

    for (key in oldMap.keys) {
        println(key)
        println(oldMap[key])
    }

    for ((key, value) in oldMap.entries) {
        println(key)
        println(value)
    }
}