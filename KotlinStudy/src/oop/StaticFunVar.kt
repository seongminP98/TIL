package oop

fun main() {
    val p = Person2.newBaby("babyHong")
    val p2 = Person2("hong", 20)
    p2.gender = "male"
    println(p2.gender)
    println(p)

    Singleton.a += 10
    println(Singleton.a)
}

class Person2  constructor(
    var name: String,
    var age: Int
) {
    lateinit var gender: String
    companion object {
        private const val MIN_AGE = 1
        fun newBaby(name: String): Person2 {
            return Person2(name, MIN_AGE)
        }
    }

    override fun toString(): String {
        return "Person2(name='$name', age=$age)"
    }
}

object Singleton {
    var a: Int = 0
}