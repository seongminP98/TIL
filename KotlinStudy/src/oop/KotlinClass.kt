package oop

//class Person (name: String, age: Int){ // 생성자는 위에 위치 ()앞에 constructor 생략 가능
//
//    val name = name
//    var age = age
//}

fun main() {
    val person = Person("hong", 40)
    println(person.name)
    person.age = 10
    println(person.age)

    val person2 = Person("jang")

    print(person.isAdult)
}

class Person(  // 생성자에서 프로퍼티를 만들 수 있다.
    val name: String,
    var age: Int
) { // body 에 아무것도 없으면 생략 가능
    init { // 이 클래스가 초기화 되는 시점에 한번 호출
        if (age <= 0) {
            throw IllegalArgumentException("나이는 ${age}일 수 없습니다.")
        }
    }

    constructor(name: String) : this(name, 1) // 부 생성자 (거의 사용 안함)

    val isAdult: Boolean
        get() = this.age >= 20
}