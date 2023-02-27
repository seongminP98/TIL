package oop

fun main() {
    val dto1 = PersonDto("hong", 40)
    val dto2 = PersonDto("hong", 50)
    val dto3 = PersonDto("hong", 50)
    println(dto1 == dto2)
    println(dto2 == dto3)
    println(dto1)
}

data class PersonDto(
    val name: String,
    val age: Int
)