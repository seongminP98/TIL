package fp

fun main() {
    val fruits = listOf(
        Fruit("사과", 1_000),
        Fruit("사과", 1_200),
        Fruit("사과", 1_200),
        Fruit("사과", 1_500),
        Fruit("바나나", 3_000),
        Fruit("바나나", 3_200),
        Fruit("바나나", 2_500),
        Fruit("수박", 10_000)
    )

    // 람다를 만드는 방법 1
    val isApple: (Fruit) -> Boolean = fun(fruit: Fruit): Boolean { // 이름없는 함수 (람다)
        return fruit.name == "사과"
    }

    // 람다를 만드는 방법 2
    val isApple2 = { fruit: Fruit -> fruit.name == "사과" }

    // 람다를 직접 호출하는 방법 1
    isApple(fruits[0])

    // 람다를 직접 호출하는 방법 2 : invoke 사용
    isApple.invoke(fruits[0])

    filterFruits(fruits, isApple)


    // 중괄호와 화살표를 쓰는 함수를 파라미터로 받을 때 마지막에 있으면 소괄호 밖으로 뺄 수 있다.
//    filterFruits(fruits, { fruit: Fruit -> fruit.name == "사과" })
    filterFruits(fruits) { fruit: Fruit -> fruit.name == "사과" }

    // filterFruits 에서 타입을 추론할 수 있어 타입 생략 가능
    // fruit -> fruit.name 을 it.name 으로 사용가능 (파라미터가 한개인 경우. fruit 한개)
    filterFruits(fruits) { it.name == "사과" }
}

private fun filterFruits(
    fruits: List<Fruit>, filter: (Fruit) -> Boolean // filter라는 함수 자체를 파라미터로 받음
): List<Fruit> {
    val results = mutableListOf<Fruit>()
    for (fruit in fruits) {
        if (filter(fruit)) {
            results.add(fruit)
        }
    }
    return results
}

private fun filterFruits2(
    fruits: List<Fruit>, filter: (Fruit) -> Boolean
): List<Fruit> {
    return fruits.filter(filter)
}