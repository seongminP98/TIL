package fp

fun main() {
    val str = "ABC"
    println(str.lastChar())

    val a = 5
    a.add(3)
    a.add2(3)
    a add2 3 // add2는 infix 함수수
}
fun String.lastChar(): Char {
    return this[this.length - 1]
}

fun Int.add(other: Int): Int {
    return this + other
}

infix fun Int.add2(other: Int): Int {
    return this + other
}