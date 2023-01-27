# @JsonView와 @JsonFilter

## @JsonView

- @JsonView 어노테이션을 활용하면 계층적인 부분 렌더링이 가능하다.

```kotlin
interface Views {
    interface List
    interface Get: List
}

class User(
    @JsonView(Views.List::class)
    val id: UUID = UUID.randomUUID(),
    @JsonView(Views.List::class)
    val email: String,
    @JsonView(Views.List::class)
    val name: String,
    @JsonView(Views.Get::class)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @JsonView(Views.Get::class)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
```

- 위와 같이 선언할 경우 `@JsonView(Dto.Views.List::class)`로 데이터를 처리하게 되면 모든 데이터를 유저 엔티티에서 `id`, `email`, `name` 만을 Jackson 라이브러리가 serialize 하게 된다. `@JsonView(Dto.Views.Get::class)`으로 지정하면 유저 엔티티의 모든 데이터가 serialize 된다.

```kotlin
@RestController
@RequestMapping
class BookController {
    private companion object {
        val user = Dto.User(
            name = "Park",
            email = "park@gmail.com"
        )
    }

    @GetMapping("/get")
    @JsonView(Dto.Views.Get::class)
    fun jsonViewGet() = user

    @GetMapping("/list")
    @JsonView(Dto.Views.List::class)
    fun jsonViewList() = listOf(user)
}
```

- `/get` 요청에서는 `id`, `email`, `name`, `createAt`, `updateAt` 필드 조회
- `/list` 요청에서는 `id`, `email`, `name` 필드 조회

## @JsonFilter

- `@JsonView`의 경우 지정해놓은 계층 구조 혹은 뷰가 아닐 경우 필드 선택 자체가 불가능하다. 결국 서버 어플리케이션에서 정해놓은 구조로만 뷰를 응답 받을 수 있다. 실제 요청에 부합하는 앙답 값만 전달하기 위해서는 `@JsonFilter`와 Spring MVC의 MappingJacksonValue를 활용하면 가능하다.

```kotlin
@JsonFilter("bookFilter")
class Book (
    val isbn: String,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

class GetReq {
    var fields: List<String> = emptyList()
}
```

```kotlin
@RestController
@RequestMapping
class BookController {
    private companion object {
        val book: Dto.Book = Dto.Book(
            isbn = UUID.randomUUID().toString(),
            title = "Title",
            content = "Content"
        )
    }

    @GetMapping("/json-filter")
    fun jsonFilter(reqDto: GetReq) = MappingJacksonValue(book).apply {
        filters = SimpleFilterProvider().also {
            it.addFilter("bookFilter",
                if (reqDto.fields.isNotEmpty()) SimpleBeanPropertyFilter.filterOutAllExcept(reqDto.fields.toSet())
                else SimpleBeanPropertyFilter.serializeAll()
            )
        }
    }
}
```

- get(”/json-filter”).param(”fields”, “isbn, title”)로 요청 시 `isbn`, `title` 필드만 응답한다.

참고
[https://github.com/thefeeling/spring-code/tree/develop/spring-mvc-jacksonview](https://github.com/thefeeling/spring-code/tree/develop/spring-mvc-jacksonview)
