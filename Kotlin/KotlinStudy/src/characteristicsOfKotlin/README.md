# 코틀린의 특성

## 코틀린의 이모저모

### Type Alias

- 긴 이름의 클래스 혹은 함수 타입이 있을 때 축약하거나 더 좋은 이름을 쓰고 싶을 때 사용

```kotlin
typealias FruitFilter = (Fruit) -> Boolean

fun filterFruits(fruits: List<Fruit>, filter: FruitFilter) { ... }
```

- 이름 긴 클래스를 컬렉션에 사용할 때도 간단히 줄일 수 있다.

### as import

- 다른 패키지의 같은 이름 함수를 동시에 가져오고 싶을 때 사용
- 어떤 클래스나 함수를 임포트 할 때 이름을 바꾸는

### 구조분해

- 복합적인 값을 분해하여 여러 변수를 한 번에 초기화하는 것

```kotlin
data class Person(
    val name: String,
    val age: Int
)

fun main() {

    val person = Person("hong", 40)
    val (name, age) = person
    println("이름 $name, 나이 $age")
}
```

- `data class`는 `componentN`이란 함수를 자동으로 만들어준다.
- `data class` 가 아닌 경우 `componentN` 함수를 직접 구현 해줄 수도 있다.

### Jump와 Label

- return : 기본적으로 가장 가까운 enclosing function 또는 익명함수로 값이 반환된다.
- break : 가장 가까운 루프가 제거된다.
- continue : 가장 가까운 루프를 다음 step으로 보낸다.
- `forEach` 구문에서는 `break`와 `continue` 사용 불가
- `forEach` 구문에서 `break`를 사용하고 싶다면 `run` 블럭으로 감싸고 `return@run`을 사용하자.
    
    ```kotlin
    run {
        numbers.forEach {
            if (it == 2) {
                return@run
            }
            println(it)
        }
    }
    ```
    
- Label
    - 특정 expression에 `라벨이름@` 을 붙여 하나의 라벨로 간주하고 `break`, `continue`, `return` 등을 사용하는 기능

### TakeIf와 TakeUnless

- `takeIf` : 주어진 조건을 만족하면 그 값을, 그렇지 않으면 null이 반환된다.
    - `number.takeIf { it > 0 }`
- takeUnless : 주어진 조건을 만족하지 않으면 그 값을, 그렇지 않으면 null이 반환된다.
    - `number.takeUnless { it <= 0 }`

## scope function

### scope function이란?

- scope : 영역, function : 함수 → scope function : 영역 함수 = 일시적인 영역을 형성하는 함수
- 람다를 사용해 일시적인 영역을 만들고 코드를 더 간결하게 만들거나, method chaning에 활용하는 함수를 scope function이라고 한다.

```kotlin
person?.let {
    println(it.name)
    println(it.age)
}
```

- Safe Call (`?.`) 사용 : `person`이 `null` 이 아닐 때 `let` 호출
- let : 확장함수. 람다를 받아 람다 결과를 반환한다.
    - 람다( `{ }` ) 안에서 `it` 을 통해 `person`에 접근한다.

### scope function의 분류

- `let`, `run` : 람다의 결과를 반환
- `also`, `apply` : 람다의 결과와 무관하게 객체 그 자체를 반환

- `let`, `also` : 람다 안에서 `it` 사용
- `run`, `apply` : 람다 안에서 `this` 사용

- `this` : 생략이 가능한 대신, 다른 이름을 붙일 수 없다.
- `it` : 생략이 불가능한 대신, 다른 이름을 붙일 수 있다.
- `let`은 일반 함수를 받고, `run` 은 확장 함수를 받는다.

### 언제 어떤 scope function을 사용해야 할까?

- `let` :
    - 하나 이상의 함수를 call chain 결과로 호출할 때
    - non-null 값에 대해서만 code block을 실행시킬 때
    - 일회성으로 제한된 영역에 지역 변수를 만들 때
- `run` :
    - 객체 초기화와 반환 값의 계산을 동시에 해야 할 때
        - 객체를 만들어 DB에 바로 저장하고, 그 인스턴스를 활용할 때
- `apply` :
    - 객체 설정을 할 때에 객체를 수정하는 로직이 call chain 중간에 필요할 때
- `also` :
    - 객체를 수정하는 로직이 call chain 중간에 필요할 때
- `with` :
    - 특정 객체를 다른 객체로 변환해야 하는데, 모듈 간의 의존성에 의해 정적 팩토리 혹은 toClass 함수를 만들기 어려울 때
    
    ```kotlin
    return with(person) {
    		PersonDto(
    				name = name,
    				age = age
    		)
    }
    ```
    

### scope function과 가독성

```kotlin
// Effective kotlin 예시

// 1번 코드
if (person != null && person.isAdult) {
		view.showPerson(person)
} else {
		view.showError()
}

// 2번 코드
person?.takeIf { it.isAdult }
		?.let(view::showPerson)
		?: view.showError()
```

1. 2번 코드는 이해하기 어렵다.
2. 1번 코드가 디버깅이 쉽다.
3. 1번 코드가 수정도 더 쉽다.
- 2번 코드에서 `view::showPerson`이 `null`을 반환하면?
    - `?:` 연산자에 의해 `view.showError()`도 실행된다.