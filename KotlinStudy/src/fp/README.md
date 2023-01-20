# FP

## 배열과 컬렉션

### 배열

- `array.indices` : 0부터 마지막 index까지의 Range
    - `for (i in array.*indices*)`
- `array.withIndex()` : 인덱스와 값을 한 번에 가져올 수 있다.
    - `for ((idx, value) in newArray.*withIndex*())`
- `array.plus(element)` : 배열에 값을 추가. (값이 추가된 새로운 배열 리턴)

### 컬렉션

- 컬렉션을 만들어줄 때 불변인지 가변인지 설정해야 한다.
- 가변 (Mutable) 컬렉션 : 컬렉션에 element를 추가, 삭제할 수 있다.
    - `MutableList`, `MutableSet`, `MutableMap`
- 불변 컬렉션 : 컬렉션에 element를 추가, 삭제할 수 없다.
    - `List`, `Set`, `Map`
- 불변 컬렉션이라 하더라도 Reference Type인 Element의 필드는 바꿀 수 있다.

### List

- 빈리스트를 만들고 싶다면 emptyList<>()
- 가변 리스트의 기본 구현체는 `ArrayList`이다.

### Set

- 자료구조적 의미만 다르고 List와 똑같다.
- 불변집합 : `setOf()`
- 가변집합 : `mutableSetOf()`
    - 기본 구현체는 `LinkedHashSet`

### Map

- 가변 Map : `mutableMapOf<Int, String>()`
    - `map[2] = "TUESDAY"` put 대신 이렇게 사용 가능
- 불변 Map : `mapOf(key to value)`
    - *`mapOf*(1 *to* "MONDAY", 2 *to* "TUESDAY")`

### 컬렉션의 null 가능성

- List<Int?> : 리스트에 null이 들어갈 수 있지만, 리스트는 절대 null이 아니다.
- List<Int>? : 리스트에는 null이 들어갈 수 없지만, 리스트는 null일 수 있다.
- List<Int?>? : 리스트에 null이 들어갈 수도 있고, 리스트가 null일 수도 있다.

## 다양한 함수

### 확장함수

- 어떤 클래스 안에 있는 메소드처럼 호출할 수 있지만, 함수는 밖에 만들 수 있게 한 함수.
- `fun 확장하려는클래스.함수이름(파라미터) : 리턴타입 { }`
    - `this` 를 이용해 실제 클래스 안의 값에 접근
    
    ```kotlin
    fun String.lastChar(): Char {
        return this[this.length - 1]
    }
    ```
    
    - `this` : 수신객체
    - `확장하려는클래스` : 수신객체 타입
    
    ```kotlin
    val str = "ABC"
    println(str.lastChar())
    ```
    
    - `String`의 멤버함수처럼 사용 가능
- 캡슐화가 깨지지 않게 하기 위해 확장함수에서는 클래스에 있는 `private` 또는 `protected` 멤버를 가져올 수 없다.
- 확장함수와 멤버함수의 시그니처가 동일하면 멤버함수가 우선적으로 호출된다.
- 확장함수 오버라이드 : 해당 변수의 현재 타입 즉, 정적인 타입에 의해 어떤 확장함수가 호출될지 결정된다.
- Java에서는 Kotlin의 확장함수를 정적 메소드를 부르는것처럼 사용하면 된다.

### infix 함수 (중위함수)

- 앞에서 본 step, downTo가 infix 함수

```kotlin
infix fun Int.add2(other: Int): Int {
    return this + other
}

a add2 3 // 이렇게 사용
```

### inline 함수

- 함수가 호출되는 대신, 함수를 호출한 지점에 함수 본문을 그대로 복붙
- 왜 사용?
    - 함수를 파라미터로 전달할 때에 오버헤드를 줄일 수 있다.
    - 성능 측정과 함께 신중히 사용하자.

### 지역함수

- 함수 안에 함수를 선언
- 함수로 추출하면 좋을 것 같은데 이 함수를 지금 함수 내에서만 사용하고 싶을 때 지역함수를 사용한다.
- 하지만 depth가 깊어지기도 하고, 코드가 깔끔하지 않다.

## 람다

- 자바에서 람다를 사용하면 ‘메소드 자체를 직접 넘겨주는 **것처럼**’ 쓸 수 있다.
    - Java에서 함수는 변수에 할당되거나 파라미터로 전달할 수 없다. 자바에서 함수는 2급 시민

### 코틀린에서의 람다

- Java와는 근본적으로 다른 한가지
    - 코틀린에서는 함수가 그 자체로 값이 될 수 있다.
    - 변수에 할당할수도, 파라미터로 넘길수도 있다.

```kotlin
// 람다를 만드는 방법 1
val isApple = fun(fruit: Fruit): Boolean {
    return fruit.name == "사과"
}

// 람다를 만드는 방법 2
val isApple2 = { fruit: Fruit -> fruit.name == "사과" }

// 람다를 직접 호출하는 방법 1
isApple(fruits[0])
    
// 람다를 직접 호출하는 방법 2 : invoke 사용
isApple.invoke(fruits[0])
```

```kotlin
val isApple: (Fruit) -> Boolean = fun(fruit: Fruit): Boolean { // 이름없는 함수 (람다)
    return fruit.name == "사과"
}
```

- 함수의 타입 : (파라미터 타입..) → 반환 타입
- 람다를 작성할 때, 람다의 파라미터를 it 으로 직접 참조할 수 있다.
- 람다를 여러줄 작성할 수 있고, 마지막 줄의 결과가 람다의 반환값이 된다.

### Closure

- Java에서는 람다를 쓸 때 사용할 수 있는 변수에 제약이 있다.
    - final인 변수 혹은 실직적으로 final인 변수만 사용할 수 있다.

```java
String targetFruitName = "바나나";
targetFruitName = "수박";
filterFruits(fruits, (fruit) -> targetFruitName.equals(fruit.getName()));

// -> Variable used in lambda expression should be final or effectively final
```

- Kotlin에서는 아무런 문제 없이 동작한다.

```kotlin
vartargetFruitName = "바나나";
targetFruitName = "수박";
filterFruits(fruits) { it.name == targetFruitName };
```

- Kotlin에서는 람다가 시작하는 지점에 참조하고 있는 변수들을 모두 포획하여 그 정보를 가지고 있다.
    - 이렇게 해야만, 람다를 진정한 1급 시민으로 간주할 수 있다.
    - 이 데이터 구조를 **Closure**라고 부른다.

### try with resources의 use

```kotlin
public inline fun <T : Closeable?, R> T.use(block: (T) -> R): R { ... }
```

- `Closeable` 구현체에 대한 확장함수이다.
    - 확장함수? → `타입.함수이름` (`T.use`) 으로 함수를 확장
    - `Closeable`을 구현한 타입 `T`에 대한 확장함수.
- `inline` 함수이다.
- 람다를 받게 만들어진 함수이다.
    - 이름은 block. T타입의 파라미터를 받고 R타입을 리턴하는 함수 즉, 람다

## 컬렉션을 함수형으로 다루는 방법

### 필터와 맵

- `filter` : `fruits.filter{ fruit → fruit.name == “사과" }`
- `filterIndexed` : `filter`에서 index가 필요할 때 사용
- `map` : `filter`로 필터링한 다음 `map`을 통해 필요한 값만 가져옴
- `mapIndexed` : `map`에서 index가 필요할 때 사용
- `mapNotNull` : `map`의 결과가 `null`이 아닌것만 가져옴

### 다양한 컬렉션 처리 기능

- `all` : 조건을 모두 만족하면 true 그렇지 않으면 false
- `none` : 조건을 모두 불만족하면 true 그렇지 않으면 false
- `any` : 조건을 하나라도 만족하면 true 그렇지 않으면 false
- `count` : 개수를 센다 (list의 size)
- `sortedBy` : 오름차순 정렬을 한다.
- `sortedByDescending` : 내림차순 정렬을 한다.
- `distinctBy` : 변형된 값을 기준으로 중복을 제거한다.
- `first` : 첫번째 값을 가져온다 (무조건 null이 아니어야함) ↔ `last`
- `firstOrNull` : 첫번째 값 또는 null을 가져온다.  ↔ `lastOrNull`

### List를 Map으로

- `groupBy` : `val map: Map<String, List<Fruit>> = fruits.groupBy { fruit → fruit.name }`
    - 이름을 기준으로 그룹핑이 된다.
- `groupBy` : `val map: Map<String, List<Long>> = fruits.groupBy({ fruit → fruit.name }, { fruit → fruit.factoryPrice })`
    - key에는 과일 이름, value에는 리스트 출고가가 들어간다.
- `associateBy` : val map: Map<Long, Long> = fruits.associateBy({ fruit → fruit.id }, { fruit → fruit.factoryPrice })
    - key에는 과일 아이디, value에는 출고가가 들어간다.

### 중첩된 컬렉션 처리

- `flatMap` 사용
    - List List가 단일 List로 변경
    - 조건을 람다로 걸어줄 수 있음
- `flatten()`
    - 중첩된 컬렉션이 그냥 중첩이 해제된 상태로 바뀜