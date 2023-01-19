package VarTypeOp

fun main() {
    val number1: Int? = 3
    val number2: Long = number1?.toLong() ?: 0

    val str = """
        ABC
        EFG
        XYZ
    """.trimIndent()
    println(str)
}

fun printAgeIfPerson(obj: Any) {
    if (obj is Person) { // !is 를 사용하면 obj 가 Person 아니라면
        val person = obj as Person
        println(person.age)

        println(obj.age) // 위 2줄을 이렇게 나타낼 수 있음
    }
}

fun printAgeIfPerson2(obj: Any?) { // obj null 가능
    val person = obj as? Person
    println(person?.age)
}

class Person {
    val name: String = "hong"
    val age: Int = 50
}