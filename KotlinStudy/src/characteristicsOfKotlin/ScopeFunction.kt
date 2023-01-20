package characteristicsOfKotlin

fun main() {
    val person = Person("hong", 50)

    val value1 = person.let {
        it.age
    }

    val value2 = person.run {
        this.age
    }

    val value3 = person.also {
        it.age
    }

    val value4 = person.apply {
        this.age
    }

    val personHong = createPerson("hong", 50, "running")
    println("${personHong.name} ${personHong.age} ${personHong.hobby}")
}

fun printPerson(person: Person?) {
//    if (person != null) {
//        println(person.name)
//        println(person.age)
//    }

    // 위의 코드를 let 을 사용해 리팩토링
    person?.let {
        println(it.name)
        println(it.age)
    }
}

fun createPerson(
    name: String,
    age: Int,
    hobby: String
): Person {
    return Person(
        name = name,
        age = age
    ).apply {
        this.hobby = hobby
    }
}