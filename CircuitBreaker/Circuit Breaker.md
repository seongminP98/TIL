# Circuit Breaker

## Circuit Breaker란?

Fault Tolerance(=장애 허용 시스템)에서 사용되는 대표적인 패턴으로써 서비스에서 타 서비스 호출 시 **에러**, **응답지연**, **무응답**, **일시적인 네트워크 문제** 등 요청이 실패하는 경우에 Circuit을 오픈하여 메시지가 다른 서비스로 전파되지 못하도록 막고 미리 정의해놓은 Fallback Response를 보내어 서비스 장애가 전파되지 않도록 하는 패턴이다. 

- 상태 정상
    - Client → Service A → Circuit Breaker → Service B
- 상태 장애상황
    - Client → Service A <-> Circuit Breaker
    - 장애상황이므로 Fallback message 처리
    - Circuit Breaker에 의해서 Service B까지 요청이 도달하지 않음

## Circuit Breaker 상태

- `CLOSE`: 초기 상태이며 모든 접속은 평소와 같이 실행된다.
- `OPEN`: 에러율이 임계치를 넘어서면 `OPEN` 상태가 되며 모든 접속은 차단(fail fast) 된다. (실제 요청을 날리지 않고 바로 에러를 발생시킴)
- `HALF_OPEN`: `OPEN` 상태 중간에 한번씩 요청을 날려 응답이 성공인지를 확인하는 상태이며 `OPEN` 후 일정 시간이 지나면 `HALF_OPEN` 상태가 된다. 접속을 시도하여 성공하면 `CLOSE`, 실패하면 `OPEN`으로 되돌아감

---

- `OPEN` 상태에서 `waitDurationInOpenState` 만큼의 시간 후에 `HALF_OPEN` 상태가 된다.
- `HALF_OPEN` 상태에서 `permittedNumberOfCallsInHalfOpenState` 만큼의 요청을 받고 open될지, close 될지 결정
- slowCalls와 failedCalls 는 따로 계산. 둘 중 하나가 임계값을 초과해야 circuit open

## Fallback Method

- try/catch 블록처럼 동작한다.
- Fallback method는 Circuit-Breaker가 적용된 메서드와 동일한 클래스에 배치되어야 하며, 하나의 예외 매개변수와 함께 동일한 메서드 시그니처를 가져야 한다.
- Circuit-Breaker가 CLOSED 상태일 때도 예외가 발생하게되면 fallback method가 호출된다.
- Circuit-Breaker가 OPEN 상태일 때는 CallNotPermittedException을 가지고 fallback method가 호출된다.
- Fallback 메서드에서는 단순히 예외를 던지거나 기본 값을 반환할 수도 있으며, 대체 결과를 반활 할 수도 있다.
    - 과제에서는 기존 코드에서 처리하는 방식을 그대로 사용
        - null을 반환하는 경우
        - RuntimeException을 던지는 경우
        - log만 찍는 경우

## 적용

- `org.springframework.boot:spring-boot-starter-aop`,
    
    `org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j`  추가
    
    - resilience4j circuitbreaker는 AOP를 통해 메서드 실행 전후의 상태를 체크하고, Circuit Breaker가 열리거나 닫히도록 동작한다. 이를 위해 aop 라이브러리가 필요.
- Circuit-Breaker 설정을 위해 application.yml 파일 또는 config 파일을 이용
    - `slidingWindowType` **: COUND_BASED(default) 또는 TIME_BASED
    - `slidingWindowSize` : circuitbreaker의 상태가 CLOSED 일 때 요청의 결과를 기록하기 위한 크기
    - `permittedNumberOfCallsInHalfOpenState` : HALF_OPEN 상태에서 허가되는 요청 수
    - `waitDurationInOpenState` : 서킷의 상태가 OPEN에서 HALF_OPEN으로 변경되기 전 circuitbreaker가 기다리는 시간
    - `failureRateThreshold` : 에러 비율. 해당 값 이상으로 에러 발생 시 circuitbreaker OPEN
- `@CircuitBreaker(name = “circuitBreakerName”, fallbackMethod = “fallback”)` 어노테이션을 이용해 Circuit-Breaker 적용
    - Circuit-Breaker가 적용된 메소드에서 예외를 try/catch를 이용해 처리하게 된다면, Circuit-Breaker가 원하는 모습으로 작동하지 않을 수 있다.
- fallbackMethod
    - Circuit-Breaker가 CLOSED인 상태에서도 예외가 발생하면 fallbackMethod가 실행되므로 Circuit-Breaker가 OPEN인지 CLOSED인지 구분을 하지 못하기 때문에 오버라이딩을 이용해 구현

### @CircuitBreaker의 동작

```kotlin
@Service
class Service(
		private val restTemplate: RestTemplate,
) {
		fun serviceA(id: String): String {
				//...
				request(id)
				//...
		}

		@CircuitBreaker(name = "serviceA", fallbackMethod = "fallbackA")
		fun request(id: String): String {
				val response = restTemplate.exchange("api/test/$id", HttpMethod.GET, null, String::class.java)
				return response.body.toString()
		}
}
```

- 처음 적용할 때 위 코드와 같이 restTemplate을 호출하는 부분을 따로 메서드로 추출하고 `@CircuitBreaker`을 적용하니 동작을 하지 않았다.
- `@CircuitBreaker` 어노테이션을 사용하면, 해당 메서드는 AOP를 이용해 프록시로 감싸지고 실행된다.
    
    `request()` 메소드에는 `@CircuitBreaker` 어노테이션이 적용되어 있지만, 해당 어노테이션을 가진 메소드를 호출하는 부분이 `serviceA()` 내부에 있기 때문에, 프록시에 의해 감싸지지 않고 직접 호출되어 실행된다. → 그래서 Circuit-Breaker가 제대로 동작하지 않는다.
    
- 아래 적은 1번 테스트를 통해 확인

## Circuit Breaker 테스트

### 1. 실제 동작하는 테스트용 서버를 만들어 테스트

- 로컬에서 동작하는 테스트용 API 서버를 만들고, Circuit-Breaker가 적용된 서비스에서 테스트용 API 서버에 요청을 하는 방식
- 테스트용 서버가 local에 있어야만 테스트가 가능하기 때문에, 실제로 Circuit-Breaker가 제대로 동작하는지 확인하기 위해 작성

### 2. Circuit Breaker만 테스트

- Circuit-Breaker가 적용된 서비스 메서드를 테스트 하는 것이 아닌 MockRestServiceServer를 이용해 Circuit Breaker 자체만을 테스트

### 3. Circuit Breaker가 적용된 서비스 메서드 테스트

- 테스트를 할 Service 객체를 `@Autowired`를 이용해 주입받고, `MockRestServiceServer`를 이용해 해 응답 값을 만들어서 테스트
    - 응답이 성공해 Circuit-Breaker가 작동 안하는 경우(CLOSED 상태)
    - 응답이 실패해 Circuit-Breaker가 작동 하고(OPEN) 일정 시간이 지난 후 HALF_OPEN 상태가 되었을 때 응답이 성공해 상태가 CLOSED로 바뀌는 경우
- Service 객체를 `@Autowired`를 이용해 주입받지 않고, 테스트 내에서 직접 만들어서 할 경우 Circuit-Breaker가 동작하지 않는다.
    - 이것도 프록시 방식으로 동작하기 때문에 생긴 문제라고 추측중.