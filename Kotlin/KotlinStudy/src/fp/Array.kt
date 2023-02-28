package fp

fun main() {
    val array = arrayOf(100, 200)

    for (i in array.indices) {
        println("$i ${array[i]}")
    }

    val newArray = array.plus(300) // 기존 배열에 element 추가해서 새로운 배열 생성

    for ((idx, value) in newArray.withIndex()) {
        println("$idx $value")
    }

}