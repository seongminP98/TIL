# 코드를 제어하는 방법

## 제어문

### if문

- if문 하나는 Java와 똑같다. `if() { }`
- Java에서 if-else는 Statement이지만, Kotlin에서는 Expression이다.
    - Statement : 프로그램의 문장, 하나의 값으로 도출되지 않는다.
    - Expression : 하나의 값으로 도출되는 문장
    - 그래서 3항 연산자가 없다.
    - `if - else if - else` 도 마찬가지

```kotlin
fun getPassOrFail(score: Int): String {
		return if (score >= 50) {
				"P"
		} else {
				"F"
		}
}

fun getGrade(score: Int): String {
    return if (score >= 90) {
        "A"
    } else if (score >= 80) {
        "B"
    } else if (score >= 70) {
        "C"
    } else {
        "D"
    }
}
```

- 어떠한 값이 특정 범위에 포함되어 있는지, 포함되어 있지 않은지

```kotlin
if (0 <= score && score <= 100) // Java에서 사용. Kotlin에서도 사용 가능
if (score in 0..100) // .. 연산자를 이용해 사용 가능
```

### switch와 when

```kotlin
when(값) {
		조건부 -> 어떠한 구문
		조건부 -> 어떠한 구문
		else -> 어떠한 구문
}

fun getGradeWithSwitch(score: Int): String {
    return when (score / 10) {
        9 -> "A"
        8 -> "B"
        7 -> "C"
        else -> "D"
    }
}

fun getGradeWithSwitch2(score: Int): String {
    return when (score) {
        in 90..99 -> "A"
        in 80..89 -> "B"
        in 70..79 -> "C"
        else -> "D"
    }
}
```

- 조건부에는 어떠한 expression이라도 들어갈 수 있다. (e.g.  is Type)

```kotlin
fun startsWithA(obj: Any): Boolean {
    return when (obj) {
        is String -> obj.startsWith("A")
        else -> false
    }
}
```

- when 을 early return 처럼 활용 가능

```kotlin
fun judgeNumber2(number: Int) {
    when {
        number == 0 -> println("주어진 숫자는 0입니다.")
        number % 2 == 0 -> println("주어진 숫자는 짝수입니다.")
        else -> println("주어진 숫자는 홀수입니다.")
    }
}
```

- when은 **Enum Class** 혹은 **Sealed Class**와 함께 사용할 경우, 더욱더 진가를 발휘한다.

## 반복문

### for each

- Java에서는 `:` 사용
- Kotlin에서는 `in` 사용

### 전통적인 for문

- `..` 연산자 사용
    - e.g. `i in 1..3` : 1부터 3
- 내려가는 경우는 `downTo` 연산자 사용
    - e.g. `i in 3 *downTo* 1`
- 2칸씩 올라가는 경우는 `step` 연산자 사용
    - e.g. `i in 1..5 *step* 2`

### Progression과 Range

- `..` 연산자는 범위를 만들어내는 연산자
    - `1..3` 의 의미는 “1에서 시작하고 3으로 끝나는 등차수열을 만들어 줘” 라는 뜻
- `step`과 `downTo`도 함수이다. (중위 호출 함수)
    - 변수.함수이름(argument) 대신 변수 함수이름 argument

### while

- Java와 동일.

## 예외

### try catch finally 구문

- Java와 `try catch`자체 문법은 똑같음
    - `catch`에서 타입이 뒤에 위치
    - `throw`에서 `new`를 사용하지 않음
- `try catch`가 Expression이기 때문에 `return`을 한번만 사용 가능
- `try catch finally`는 Java와 완전 동일

### Checked Exception과 Unchecked Exception

- Kotlin에서는 Checked Exception과 Unchecked Exception을 구분하지 않는다. 모두 Unchecked Exception 이다.

### try with resources

- 코틀린에서는 try with resources가 없다. 대신 코틀린의 언어적 특징을 활용해 close를 호출해준다. use 사용

```kotlin
class FilePrinter {
    fun readFile(path: String) {
        BufferedReader(FileReader(path)).use { reader ->
            println(reader.readLine())
        }
    }
}
```

## 함수

### 함수 선언 문법

- 접근 지시어 `public`은 생략 가능
- 함수가 하나의 결과값이면 block( `{ }` ) 대신 `=` 사용 가능 (`return` 생략)
- `=` 을 사용하는 경우 반환 타입 생략 가능
- block( `{ }` ) 을 사용하는 경우에는 반환 타입이 Unit이 아니라면 명시적으로 반환 타입을 작성해주어야 한다.

```kotlin
fun max(a: Int, b: Int) = if (a > b) a else b
```

- 함수는 클래스 안에 있을 수도, 파일 최상단에 있을 수도 있다. 또한, 한 파일 안에 여러 함수들이 있을 수도 있다.

### default parameter

```kotlin
fun main() {
    repeat("Hello World")
}

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
```

- 밖에서 파라미터를 넣어주지 않으면 기본값을 사용
    - 위 예시에서는 `num`과 `useNewLine`을 안넣어줬기에 3과 true로 함수 실행

### named argument

```kotlin
repeat("Hello World", useNewLine = false)
```

- 매개변수 이름을 통해 직접 지정
- 지정되지 않은 매개변수는 기본값 사용
- builder를 직접 만들지 않고, builder의 장점을 가지게 된다.
- Kotlin에서 Java 함수를 가져다 사용할 때는 named argument를 사용할 수 없다.
    - JVM 상에서 Java가 바이트 코드로 변환됐을 때 parameter 이름을 보존하고 있지 않아 Kotlin에서는 그 이름을 통해서 가져오는 named argument를 쓸 수 없다.

### 가변인자

- 같은 타입의 여러 파라미터 받기
- Java에서는 `타입…` 사용. 배열을 직접 넣거나, comma를 이용
- Kotlin에서는 `vararg` 사용.
    - 배열을 바로 넣는 대신 스프레드 연산자(*)을 붙여주어야 한다.

```kotlin
fun main() {
		printAll("A", "B", "C")
    val array = arrayOf("A", "B", "C")
    printAll(*array)
}

fun printAll(vararg strings: String) { }
```