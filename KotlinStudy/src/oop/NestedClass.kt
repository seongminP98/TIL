package oop

fun main() {

}

class JavaHouse(
    private val address: String,
    private val livingRoom: LivingRoom
) {
    inner class LivingRoom( // inner 사용하면 바깥 클래스 참조(권장x). 사용 하지 않으면 바깥 클래스 참조 안함(권장).
        private val area: Double
    ) {
        val address: String
            get() = this@JavaHouse.address
    }
}