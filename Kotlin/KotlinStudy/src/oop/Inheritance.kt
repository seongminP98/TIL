package oop

fun main() {
    Derived(300)
}

open class Base(
    open val number: Int = 100
) {
    init {
        println("Base class")
        println(number) // 0 출력. 하위 클래스의 number 를 나타내는데 아직 Derived 는 초기화 되기 전
        // 하위 프로퍼티에서 override 하고 있는 프로퍼티에 접근하면 안된다.
    }
}

class Derived(
    override val number: Int
) : Base(number) {
    init {
        println("Derived class")
    }
}