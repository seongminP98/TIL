# DBCP(Database Connection Pool)

## DBCP를 안쓸 때 문제점

- 백엔드 서버로 API request가 오면 백엔드 서버는 데이터베이스에 쿼리 요청을 보낸다.
- 이 쿼리 요청을 보내고 응답을 받는 것은 네트워크 통신을 하는 과정이다.
- 백엔드 서버와 데이터베이스 간 네트워크 통신은 TCP 기반으로 이루어진다.
- TCP는 높은 송수신 신뢰성이 있기 때문에 통신 전에 반드시 connection을 맺는 과정이 필요하다. 통신이 끝나면 연결을 끊는다.
- TCP connection을 열거나(3-way handshake) 닫을 때(4-way handshake) 생각보다 시간을 쓴다.
- 백엔드 입장에서 매번 connection을 열고 닫는 시간적인 비용 발생 - 서비스 성능에 좋지 않다.

## DBCP의 개념과 원리

- 백엔드 어플리케이션을 띄울 때 미리 DB connection을 여러 개 맺어 놓는다.
    - 연결 된 connection을 pool처럼 관리한다.
- DB 접근이 필요할 때 pool에 있는 connection을 가져와서 DB 쿼리 요청을 보낸다.
- 쿼리 응답을 받고 close connection을 한다. → 연결을 닫는 것이 아니라 pool에서 가져왔으니 pool에 반환을 하는 것.
- connection 재사용. 열고 닫는 시간 절약

## DBCP 설정 방법

Springboot의 HikariCP와 mysql 기반

### DB 서버 설정 (mysql)

- **max_connections** : client(여기선 백엔드 서버)와 맺을 수 있는 최대 connection 수
- **wait_timeout** : connection이 inactive(노는 상태)할 때 다시 요청이 오기까지 얼마의 시간을 기다린 뒤에 close(pool에 반환) 할 것인지를 결정. 시간 내에 요청이 도착하면 0으로 초기화
    - 비정상적인 connection 종료
    - connection 다 쓰고 반환이 안됨
    - 네트워크 단절
    
    이런 상황들로 인해 wait_timeout이 필요하다. 
    

### DBCP 설정 (HikariCP)

- **minimumIdle** : pool에서 유지하는 최소한의 idle connection 수
    - idle connection 수가 minimumIdle보다 작고, 전체 connection 수도 maximumPoolSize보다 작다면 신속하게 추가로 connection을 만든다.
- **maximumPoolSize** : pool이 가질 수 있는 최대 connection 수. idel과 active(in-use) connection 합쳐서 최대 수

처음 백엔드 어플리케이션이 실행되면 minimumIdle 값 만큼 커넥션을 생성하여 풀에 유지한다.

요청이 들어와 connection이 active 상태가 된다면 maximumPoolSize보다 적고, minimumIdle 보다 크게 connection을 새로 생성한다. 

트래픽이 들어오지 않고, connection이 idle 상태가 되어서 minimumidle 값보다 커진다면 minimumIdle 값 만큼 connection을 끊어준다.

- hikariCP에서 minimumidle 값은 maximumPoolSize와 동일하다. (= pool size 고정) [권장]
    - pool size가 고정이 아니라면 트래픽이 몰려올 때 connection이 생성되기 때문에 대응을 제때 못할 수도 있음.
- **maxLifetime** : pool에서 connection의 최대 수명 시간
    - 설정된 시간을 초과하면 해당 커넥션을 자동으로 폐기하고 새로운 커넥션 객체를 생성한다. 이를 통해 오랫동안 사용되거나 유휴 상태로 머무르는 커넥션 객체를 방지하고, 성능 저하를 최소화할 수 있다.
    - maxLifetime을 넘기면 idle일 경우 pool에서 바로 제거, active인 경우 pool로 반환된 후 제거
    - DB의 connection time limit보다몇 초 짧게 설정해야 된다.
- **connectionTimeout** : pool에서 connection을 받기 위한 대기 시간
    - idle connection이 있는 경우 가져와서 사용하면 됨.
    - 하지만 모두 active 상태일 때, 트래픽이 계속 밀려오면?
        - connection은 계속 회전이 될 테니 기다리지만, connectionTimeout 값을 초과하면 더 이상 기다리지 않고 exception을 받게된다.

## 적절한 connection 수를 찾기 위한 과정

- 모니터링 환경 구축(서버 리소스, 서버 스레드 수, DBCP 등)
- 백엔드 시스템 부하 테스트
    - request per second와 avg response time 확인
- 모니터링을 하며 백엔드 서버와 DB서버의 CPU, MEM 등등 리소스 사용률을 확인한다.
    - 백엔드 서버를 늘려 해결
    - DB 서버가 문제라면 (select 쿼리 많은 경우)
        - secondary 추가
        - cache layer
        - sharding
        
        으로 해결
        
    - thread per request 모델이라면 active thread 수 확인
        - thread per request 말고도 spring webflux처럼 이벤트루프 방식에서도 스레드 수가 중요하다.
        - thread per request model
            
            클라이언트에서 들어오는 각 요청을 처리하기 위해 새 스레드가 생성되는 웹 애플리케이션 구축에 대한 접근 방식이다. 이 모델에서 웹 서버는 각 요청을 처리하기 위해 새 스레드를 생성하고 각 스레드는 독립적으로 실행되어 여러 요청을 동시에 처리할 수 있다.
            
            Spring MVC는 thread per request model이다. Spring MVC의 중앙 서블릿인 DispatcherServlet은 요청을 수신하고 적절한 컨트롤러 메서드로 dispatch하는 역할을 한다. 요청이 수신되면 DispatcherServlet은 요청을 처리할 새 스레드를 만들고 처리를 위해 요청을 스레드에 전달한다. 요청이 처리되면 스레드가 해제되어 재사용을 위해 스레드 풀로 반환된다.
            
    - 위처럼 다른 각도에서도 생각해보고 DBCP의 active connection 수 확인

이런 식으로 종합적으로 고려하면서 connection 수를 설정할 수 있다.

참고

[https://www.youtube.com/watch?v=zowzVqx3MQ4](https://www.youtube.com/watch?v=zowzVqx3MQ4)