# 변수와 타입, 연산자

## 변수

- 모든 변수에는 var / val 을 붙혀 준다.
    - var : 변경 가능
    - val : 변경 불가능 (read-only)
- 타입을 명시적으로 작성하지 않아도, 타입이 추론된다.
- Primitive Type과 Reference Type을 구분하지 않는다.
- Null이 들어갈 수 있는 변수는 타입 뒤에 `?` 를 붙여주어야 한다.
    - 아예 다른 타입으로 간주됨.
- 객체를 인스턴스화 할 때 new를 붙이지 않는다.

## Null

### Safe Call

```kotlin
val str: String? = "ABC"
str.length // 불가능
str?.length // 가능
```

- `?.`
- null이 아니면 실행하고, null이면 실행하지 않는다(그대로 null).

### Elvis

```kotlin
val str: String? = "ABC"
str?.length ?: 0
```

- `?:`
- 앞의 연산 결과가 null이면 뒤의 값을 사용

### Null 아님 단언

- nullable type이지만, 아무리 생각해도 null이 될 수 없는 경우
- `!!` 사용
- 혹시나 null이 들어오게 되면 NPE 발생

### 플랫폼 타입

- Kotlin에서 Java 코드를 사용할 때 플랫폼 타입 사용에 유의해야 한다.
    - `@Nullable`, `@NotNull` 확인. 없을 경우 주의해서 사용
    - Java 코드를 읽으며 널 가능성 확인 / Kotlin으로 wrapping

## 타입

- 코틀린은 변수를 선언할 때 타입을 지정하지 않아도 선언된 기본값을 보고 타입을 추론한다.

### 타입 변환

- Java에서 기본 타입간의 변환은 **암시적**으로 이루어질 수 있다.
- Kotlin에서 기본 타입간의 변환은 **명시적**으로 이루어져야 한다.

```kotlin
val num1 = 3
val num2: Long = num1 // type mismatch 컴파일 에러 암시적 타입 변경 X
```

- Kotline에서는 `to변환타입()`을 사용

```kotlin
val number1 = 3
val number2: Long = number1.toLong()
```

- nullable 변수면 적절한 처리가 필요

### 타입 캐스팅

- `is` 연산자를 사용해서 타입 확인 `value is Type`
- `as` 연산자를 사용해서 타입 변환 `value as Type`
    - value가 Type이면 Type으로 타입 캐스팅
    - value가 Type이 아니면 예외 발생

```kotlin
if (obj is Person) {
		val person = obj as Person
		println(obj.age) // 변환하지 않고 바로 사용 가능
}
```

- `!is` 연산자는 반대 (아니라면)

- obj가 null 이 올 수 있다면?
    - `as?` 연산자 사용 `value as? Type`
        - value가 Type이면 Type으로 타입 캐스팅
        - value가 null이면 null
        - value가 Type이 아니면 null

```kotlin
fun printAgeIfPerson2(obj: Any?) { // obj null 가능
    val person = obj as? Person
    println(person?.age)
}
```

### Kotlin의 Any

- Java의 `Object` 역할 (모든 객체의 최상위 타입)
- 모든 Primitive Type의 최상위 타입도 `Any`이다.
- `null`을 포함하고 싶다면 `Any?`로 표현
- `Any`에 `equals` / `hashCode` / `toString` 존재

### Kotline의 Unit

- Java의 `void` 역할
- `void`와 다르게 `Unit`은 그 자체로 타입 인자로 사용 가능 - 제네릭에서 확인
- 함수형 프로그래밍에서 `Unit` 은 단 하나의 인스턴스만 갖는 타입을 의미. 즉, 코틀린의 `Unit` 은 실제 존재하는 타입이라는 것을 표현

### Kotline의 Nothing

- `Nothing`은 함수가 정상적으로 끝나지 않았다는 사실을 표현하는 역할
- 무조건 예외를 반환하는 함수 / 무한 루프 등

### String interpolation / String indexing

- `${변수}`
- `$변수` 중괄호 생략 가능. 하지만 중괄호 사용하는 것이 가독성, 일괄 변환 등 좋은점이 있다.
- 여러 줄에 걸친 문자열을 작성할 때 `“”” “””` 사용

```kotlin
val str = """
        ABC
        EFG
        XYZ
""".trimIndent()
```

- Kotlin의 문자열의 특정 문자 가져올 땐 `[ ]` 사용

```kotlin
val str = "ABC"
println(str[0]) // A
```

## 연산자

- Java와 다르게 객체를 비교할 때 비교 연산자를 사용하면 자동으로 `compareTo`를 호출해준다.
- 동등성(Equality) : 두 객체의 값이 같은가?
- 동일성(Identity) : 완전히 동일한 객체인가? (주소가 같은가?)
- Kotlin에서는 동일성에 `===` 사용 동등성에 `==` 사용 (자동으로 `equals` 호출)
- 논리연산자는 Java와 완전히 동일. Java처럼 Lazy 연산 수행

### in / !in

- 컬렉션이나 범위에 포함되어 있다. 포함되어 있지 않다.

```kotlin
1 in numbers
```

### a..b

- a부터 b까지의 범위 객체를 생성한다.

### a[i]

- a에서 특정 Index i로 값을 가져온다.

### a[i] = b

- a의 특정 index i에 b를 넣는다.

- Kotline에서는 객체마다 연산자를 직접 정의할 수 있다. (연산자 오버로딩)