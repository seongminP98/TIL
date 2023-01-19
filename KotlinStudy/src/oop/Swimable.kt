package oop

interface Swimable {

    val swimAbility: Int // 구현체에서 custom getter 를 구현
        get() = 3 // default 값

    fun act() {
        println(swimAbility)
        println("어푸 어푸")
    }
}