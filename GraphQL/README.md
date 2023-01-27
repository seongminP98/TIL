# GraphQL

## REST API

- 소프트웨어간 정보를 주고받는 방식
- 메소드와 URI를 조합해서 예측 가능하고 일정한 정보와 작업을 요청하는 것
- GraphQL 이전부터 사용. 이를 보완하기 위해 GraphQL이 나옴. 용도에 따라 적합한 것 사용.
    - 받아야 하는 항목들이 많고 딱 정해져 있는 경우에는 REST API가 더 좋다.
        - GraphQL에 하나하나 다 적어서 요청하기 보다 REST API의 URL한 줄로 요청
    - REST API : 요청은 단순하고 데이터는 복잡
    - GraphQL : 요청은 복잡하지만 데이터는 효율적

### REST API의 한계

- Overfetching
    - 필요한 정보 이상의 정보를 가져옴
- Underfetching
    - 하나의 endpoint로 필요한 모든 데이터 요청을 처리하지 못함. 여러번 API 호출이 필요.

## GraphQL

- GraphQL은 API용 쿼리 언어이다. 이를 통해 클라이언트는 필요한 데이터의 구조를 정의 할 수 있으며 서버는 요청된 데이터만 반환한다.
- 클라이언트는 REST API endpoint에서 고정된 데이터 집합이 아니라 필요한 특정 데이터만 요청하면 되므로 보다 효율적인 데이터 검색이 가능하다.
- GraphQL은 단일 endpoiont 및 type system을 사용하여 복잡한 데이터를 실시간으로 업데이트하고 더 잘 처리할 수 있도록 한다.

## GraphQL 강점

- 필요한 정보들만 선택하여 받아올 수 있다.
    - Overfetching 문제 해결
    - 데이터 전송량 감소
- 여러 계층의 정보들을 한 번에 받아올 수 있다.
    - Underfetching 문제 해결
    - 요청 횟수 감소
- 하나의 endpoint에서 모든 요청을 처리
    - 하나의 URI에서 POST로 모든 요청 가능

## Query 구현

### Query 루트 타입

```graphql
type Query {
        teams: [Team]
    }
```

- 자료요청에 사용될 쿼리들을 정의
- 쿼리 명령문마다 반환될 데이터 형태를 지정
- 위 예시에서 [ ]  는 `Team`이라는 데이터가 여러개 반환이 된다는 의미이다.
    - `Team` 데이터는 아래에서 확인

### Type

```graphql
type Team {
        id: Int
        manager: String
        office: String
        extension_number: String
        mascot: String
        cleaning_duty: String
        project: String
    }
```

- 반환될 데이터의 형태를 지정
- 자료형을 가진 필드들로 구성

### Resolver

```jsx
// resol
const resolvers = {
  Query: {
    teams: () => database.teams
  }
}
```

- `Query` 란 object의 항목들로 데이터를 반환하는 함수 선언
- 실제 프로젝트에서는 MySQL 조회 코드 등

### 조건 필터링

```jsx
// resolver
Query: {
    //...
    team: (parent, args, context, info) => database.teams
        .filter((team) => {
            return team.id === args.id
        })[0],
}
```

- args로 주어진 id에 해당하는 team만 필터링하여 반환

```graphql
#Query 루트
type Query {
    ...
    team(id: Int): Team
}

#요청
query {
	team(id: 1) {
	    id
	    manager
	    office
	    extension_number
	    mascot
	    cleaning_duty
	    project
	}
}
```

### 하나의 쿼리로 여러 계층 받기(Underfetching 해결)

```jsx
// resolver
Query: {
    // ...
    teams: () => database.teams
    .map((team) => {
        team.supplies = database.supplies
        .filter((supply) => {
            return supply.team === team.id
        })
        return team
    }),
}
```

- Team 목록을 반환 시 해당하는 supplies를 `supplies` 항목에 추가

```graphql
type Team {
    id: Int
    manager: String
    office: String
    extension_number: String
    mascot: String
    cleaning_duty: String
    project: String
    supplies: [Supply]
}
```

- Team의 항목들 중 다수의 Supply 반환

## Mutaion 구현

### 삭제

```graphql
type Mutation {
    deleteEquipment(id: String): Equipment
}
```

- id를 받아 Equipment id와 일치하는 것 삭제

```jsx
// resolver
Mutation: {
      deleteEquipment: (parent, args, context, info) => {
          // 삭제 구현 후 데이터 반환
      }
}
```

```graphql
# 요청
mutation {
  deleteEquipment(id: "notebook") {
    id
    used_by
    count
    new_or_used
  }
}
```

- Equipment에서 id가 notebook인 데이터 삭제 쿼리

### 삽입

```graphql
type Mutation {
    insertEquipment(
        id: String,
        used_by: String,
        count: Int,
        new_or_used: String
    ): Equipment
}
```

- 추가할 Equipment의 요소 값들을 인자로 받고 추가된 Equipment를 반환

```jsx
// resolver
Mutation: {
    insertEquipment: (parent, args, context, info) => {
        // 데이터 삽입 후 삽입된 데이터 반환 (or args반환)
    },
}
```

```graphql
# 요청
mutation {
  insertEquipment (
    id: "laptop",
    used_by: "developer",
    count: 17,
    new_or_used: "new"
  ) {
    id
    used_by
    count
    new_or_used
  }
}
```

### 수정

```graphql
type Mutation {
    editEquipment(
        id: String,
        used_by: String,
        count: Int,
        new_or_used: String
    ): Equipment
}
```

- 수정할 Equipment의 요소 값들을 인자로 받고 수정된 Equipment를 반환

```jsx
// resolver
Mutation: {
    editEquipment: (parent, args, context, info) => {
        // 데이터 수정 후 수정된 데이터 반환
    },
}
```

```graphql
# 요청
mutation {
  editEquipment (
    id: "pen tablet",
    new_or_used: "new",
    count: 30,
    used_by: "designer"
  ) {
    id
    new_or_used
    count
    used_by
  }
}
```

## Apollo server 구성요소 모듈화

- typeDefs : 단일 변수 또는 배열로 지정 가능
- resolvers : 단일 Object 또는 Merge된 배열로 가능

[apollo-server]([https://www.apollographql.com/docs/apollo-server/api/apollo-server/](https://www.apollographql.com/docs/apollo-server/api/apollo-server/))

server-modularized-types 프로젝트 확인

## GraphQL의 기본 타입들

### 스칼라 타입

- GraphQL 내장 자료형

| 타입 | 설명 |
| --- | --- |
| ID | 기본적으로는 String이나, 고유 식별자 역할임을 나타낸다. |
| String | UTF-8 문자열 |
| Int | 부호가 있는 32비트 정수 |
| Float | 부호가 있는 부동소수점 값 |
| Boolean | true/false |

### ! : Non Null

- null을 반환할 수 없음
- e. g. `String!`

### 열거 타입(Enum)

- 미리 저장된 값들 중에서만 반환

```graphql
const typeDefs = gql`
	enum Role {
		developer
		designer
		planner
	}
	enum NewOrUsed {
		new
		used
	}
`;
```

### 리스트 타입

- 특정 타입의 배열을 반환
- e. g. `[String!]`

| 선언부 | users: null | users: [] | users: […, null] |
| --- | --- | --- | --- |
| [String] | O | O | O |
| [String!] | O | O | X |
| [String]! | X | O | O |
| [String!]! | X | O | X |

### 객체 타입

- 사용자에 의해 정의된 타입들

## 유니언과 인터페이스

### Union

- 타입 여럿을 한 배열에 반환하고자 할 때 사용

### Interface

- 유사한 객체 타입을 만들기 위한 공통 필드 타입
- 추상 타입 - 다른 타입에 implement되기 위한 타입

## 인자와 인풋 타입

- 데이터 조건들로 필터 넣어서 받아오기 가능
- 페이징 가능
    - page
    - per_page
- 별칭으로 받아오기 가능
- Mutation 시 인풋 타입 설정 가능