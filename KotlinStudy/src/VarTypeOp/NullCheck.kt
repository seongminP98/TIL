package VarTypeOp

fun main() {

    val str: String? = null
    println(str?.length ?: 0)
}

fun startsWithA1(str: String?): Boolean {
    return str?.startsWith("A") ?: throw IllegalArgumentException("null이 들어옴")
}

fun startsWithA2(str: String?): Boolean? {
    return str?.startsWith("A") // str 이 null 이면 null 반환
}

fun startsWithA3(str: String?): Boolean {
    return str?.startsWith("A") ?: false
}

fun startsWith(str: String?): Boolean { // str 이 절대 null 이 아닐 경우 !! 사용
    return str!!.startsWith("A")
}