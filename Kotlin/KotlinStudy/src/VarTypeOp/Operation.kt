package VarTypeOp

fun main() {

    val money1 = JavaMoney(2_000L)
    val money2 = JavaMoney(1_000L)

    if (money1 > money2) { // 객체간 비교에서 비교 연산자를 사용하면 compareTo를 자동으로 호출해준다.
        println("Money1이 Money2보다 금액이 큽니다.")
    }
}