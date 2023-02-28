package codeControl

fun main() {

    repeat("Hello World")
    repeat("Hello World", 3, false)
    repeat("Hello World", useNewLine = false)

    printAll("A", "B", "C")
    val array = arrayOf("A", "B", "C")
    printAll(*array)
}

fun max(a: Int, b: Int) = if (a > b) a else b

fun repeat(
    str: String,
    num: Int = 3,
    useNewLine: Boolean = true
) { // default parameter
    for (i in 1..num) {
        if (useNewLine) {
            println(str)
        } else {
            print(str)
        }
    }
}

fun printAll(vararg strings: String) {
    for (str in strings) {
        print(str)
    }
    println()
}