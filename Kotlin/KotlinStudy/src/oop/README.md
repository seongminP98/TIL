# OOP

## 클래스

### 클래스와 프로퍼티

- 생성자는 위에 위치한다.
- 프로퍼티 = 필드 + getter + setter
- 코틀린에서는 필드만 만들면 getter, setter를 자동으로 만들어준다.
- 생성자에서 프로퍼티를 만들 수 있다.
- `.필드` 를 통해 getter와 setter를 바로 호출한다.
- Java 클래스를 사용할 때도 `.필드` 사용 가능

```kotlin
class Person (
    val name: String,
    var age: Int
)
```

### 생성자와 init

- init : 생성자가 호출되는 시점에 호출된다.
    - 값을 적절히 만들어주거나, validation 하는 용도로 사용됨
- `constructor` 를 이용해 생성자 추가 (부 생성자)
    
    ```kotlin
    constructor(name: String): this(name, 1)
    ```
    
    - 최종적으로 주생성자를 `this` 로 호출해야 한다.
    - body를 가질 수 있다.

- 사실 Kotlin에서는 부생성자보다는 default parameter를 권장한다.
- Converting과 같이 어떤 객체를 다른 객체로 바꾸는 경우에 부생성자를 사용할 수 있지만, 그보다는 정적 팩토리 메소드를 추천한다.

### 커스텀 getter, setter

결국은 함수?

```kotlin
val isAdult: Boolean
    get() = this.age >= 20
```

- 함수를 써도 되고 커스텀 getter를 써도 된다.
    - 프로퍼티를 접근하는거처럼 쓴다면 커스텀 getter
    - 함수를 접근하는거처럼 쓴다면 함수
- 커스텀 getter를 사용하면 자기 자신을 변형해 줄 수도 있다.

```kotlin
class Person(
		name: String,
		var age: Int
) {
		val name: String = name
			 get() = field.uppercase()
}
```

- `get() = name.uppercase()` 를 사용하게되면 `person.name` 을 호출할 때 `name`에 대한 `getter`가 호출된다. 똑같이 안에서 `name`을 호출하면 `getter`가 호출되고.. 무한 루프 발생
    - `field` : 무한루프를 막기 위한 예약어, 자기 자신을 가리킨다.
        - backing field라고 부른다.
- 커스텀 setter

```kotlin
var name: String = name
		set(value) {
				field = value.uppercase()
		}
```

- setter 자체를 지양하기 때문에 custom setter도 잘 안쓴다.

## 상속

### 추상 클래스

- `extends` 키워드를 사용하지 않고 `:` 을 사용한다.
- 상속받을 때 상위 클래스의 생성자를 바로 호출한다.
- `override`를 필수적으로 붙여준다.
- 추상 프로퍼티가 아니라면, 상속받을 때 `open` 을 꼭 붙여야 한다.
- Java, Kotlin 모두 추상 클래스는 인스턴스화 할 수 없다.

### 인터페이스

- `default`메소드를 구현할 때 `default` 키워드 없이 구현 가능
- 추상 메소드 만들 수 있다.
- 인터페이스 구현도 `:` 을 사용한다.
- 중복되는 인터페이스를 특정할 때는 `super<타입>.함수`를 사용한다.

### 클래스를 상속받을 때 주의점

- 상속받을 때 `open` 키워드 사용해야 상속 가능
- 상위 클래스에서 하위 클래스가 `override` 하고 있는 프로퍼티를 생성자 블럭이나`init` 블럭을 쓰게 되면 이상한 값이 나온다.
    - 상위 클래스를 설계할 때 생성자 또는 초기화 블럭에 사용되는 프로퍼티에는 `open`을 피해야 한다.

### 상속 관련 키워드 4가지

- final : override를 할 수 없게 막는다. default로 보이지 않게 존재한다.
- open : override를 열어 준다.
- abstract : 반드시 override 해야 한다.
- override : 상위 타입을 오버라이드 하고 있다. Kotlin에서는 키워드로 사용한다.

## 접근 제어

### Kotlin의 가시성 제어

- public : 모든 곳에서 접근 가능
- protected : 선언된 클래스 또는 하위 클래스에서만 접근 가능
    - Kotlin에서는 패키지를 namespace를 관리하기 위한 용도로만 사용된다.
- internal : 같은 모듈에서만 접근 가능
- private : 선언된 클래스 내에서만 접근 가능

Kotlin의 기본 접근 지시어는 public

### Kotlin 파일의 접근 제어

- public : 기본값. 어디서든 접근할 수 있다.
- protected : 파일(최상단)에는 사용 불가능
- internal : 같은 모듈에서만 접근 가능
- private : 같은 파일에서만 접근 가능

### 다양한 구성요소의 접근 제어

- 생성자에 접근 지시어를 붙이려면 `constructor` 를 명시해야 한다.
- 유틸성 코드는 파일 최상단에 바로 작성하면 편리하다.
    - 자바처럼 abstract class + private constructor을 사용할 필요 없음.
- 프로퍼티의 경우 `val`이나 `var` 앞에 접근 지시어를 붙인다.
    - `private set` 이렇게 사용할 경우 custom setter에만 추가로 가시성을 부여할 수 있다.
    
    ```kotlin
    class Car (
        internal val name: String,
        private var owner: String,
        _price: Int
    ) {
        var price = _price
            private set
    }
    ```
    

### Java와 Kotlin을 함께 사용할 때 주의할 점

- `Internal`은 바이트 코드 상 `public`이 된다.
    - Java 코드에서는 Kotlin 모듈의 `internal` 코드를 가져올 수 있다.
- Kotlin의 `protected`와 Java의 `protected`는 다르다.

## object 키워드

### static 함수와 변수

- `static` 키워드가 없고 `companion object` 블럭 안의 변수와 함수가 Java의 `static` 변수와 함수처럼 사용 된다.
- `static` : 클래스가 인스턴스화 될 때 새로운 값이 복제되는 것이 아니라 정적으로 인스턴스끼리의 값을 공유
- `companion object` : 클래스와 동행하는 유일한 오브젝트이다.
- `val` 키워드만 사용하면 런타임 시에 변수 할당. `val` 앞에 `const`까지 붙이면 컴파일 시에 변수 할당(진짜 상수).
- `companion object`도 객체로 간주된다. 때문에 이름을 붙일 수도 있고, `interface`를 구현할 수도 있다.
- Java에서 Kotlin의 `companion object`를 사용하려면 `@JvmStatic`을 붙여야 한다.
    - `companion object` 이름이 있다면 이름 사용 가능

### 싱글톤

- 앞에 `object` 키워드만 붙이면 끝..

### 익명 클래스

- 특정 인터페이스나 클래스를 상속받은 구현체를 일회성으로 사용할 때 쓰는 클래스
- Java에서는 `new 타입이름()`
- Kotlin에서는 `object : 타입이름`

## 중첩 클래스

### 중첩 클래스의 종류

- static을 사용하는 중첩 클래스
    - 클래스 안에 static을 붙인 클래스 - 밖의 클래스 직접 참조 불가
- static을 사용하지 않는 중첩 클래스
    - 내부 클래스 (Inner Class) - 밖의 클래스 직접 참조 가능
    - 지역 클래스 (Local Class) - 메소드 내부의 클래스
    - 익명 클래스 (Anonymous Class) - 일회성 클래스

```
Effective Java Item24, Item86 참고

1. 내부 클래스는 숨겨진 외부 클래스 정보를 가지고 있어, 참조를 해지하지 못하는 경우
메모리 누수가 생길 수 있고, 이를 디버깅 하기 어렵다.

2. 내부 클래스의 직렬화 형태가 명확하게 정의되지 않아 직렬화에 있어 제한이 있다.

-> 클래스 안에 클래스를 만들 때는 static 클래스를 사용하라!
코틀린은 이런 가이드를 충실히 따르고 있음!
```

```kotlin
class JavaHouse(
    private val address: String,
    private val livingRoom: LivingRoom
) {
    class LivingRoom(
        private val area: Double
    )
}
```

- Kotlin은 기본적으로 바깥 클래스에 대한 연결이 없는 중첩 클래스가 만들어진다.
- 바깥 클래스를 참조하고 싶다면 `inner` 키워드를 추가한다.
    - 참조 시 `this@바깥클래스.변수` 사용

## 다양한 클래스

### Data Class

- `data class` 처럼 `data` 키워드를 붙여주면 `equals`, `hashCode`, `toString`을 자동으로 만들어준다.
- Java에서는 JDK16부터 Kotlin의 `data class` 같은 `record class`를 도입

### Enum Class

```kotlin
enum class Country(
    private val code: String
) {
    KOREA("KO"),
    AMERICA("US"),
    ;
}
```

- Enum Class는 `when` 과 함께 사용할 때 좋다.
    - `when`에서 `else`를 작성할 필요가 없다. - 컴파일러가 해당 Enum에 대해 모든 타입을 알고 있기 때문.

### Sealed Class, Sealed Interface

- 컴파일 타임 때 하위 클래스의 타입을 모두 기억한다. 즉, 런타임때 클래스 타입이 추가될 수 없다.
- 하위 클래스는 같은 패키지에 있어야 한다.
- Enum과 다른점
    - 클래스를 상속받을 수 있다.
    - 하위 클래스는 멀티 인스턴스가 가능하다. - Enum은 싱글톤으로 단일 인스턴스.
- `when` 과 함께 활용하기 좋다. - 컴파일러가 타입을 다 알고 있기 때문.
- 추상화가 필요한 Entity or DTO에 sealed class를 활용하자.
- JDK17에서도 Sealed Class가 추가됨.