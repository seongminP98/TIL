package characteristicsOfKotlin

data class Person(
    val name: String,
    val age: Int
) {
    lateinit var hobby: String
}

class Person2(
    val name: String,
    val age: Int
) {
    operator fun component1() : String {
        return this.name
    }

    operator fun component2() : Int {
        return this.age
    }
}

fun main() {

    val person = Person("hong", 40)
    val (name, age) = person
    println("이름 $name, 나이 $age")

    val person2 = Person("jang", 50)
    val (name2, age2) = person2
}

