## 요구사항
- [x] 토큰 발급 기능 (로그인) 인수 테스트 만들기
- [x] 인증 - 내 정보 조회 기능 완성하기
- [x] 인증 - 즐겨 찾기 기능 완성하기

## 요구사항 설명

### 토큰 발급 인수 테스트

- 토큰 발급(로그인)을 검증하는 인수 테스트 만들기
- AuthAcceptanceTest 인수 테스트 만들기

#### 인수 조건

```text
Feature: 로그인 기능
  
  Scenario: 로그인을 시도한다.
    Given 회원 등록되어 있음
    When 로그인 요청
    Then 로그인 됨
```

#### 요청/응답

##### request

```text
POST /login/token HTTP/1.1
content-type: application/json; charset=UTF-8
accept: application/json

{
    "password": "password",
    "email": "email@email.com"
}
```

##### response

```text
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sun, 27 Dec 2020 04:32:26 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBlbWFpbC5jb20iLCJpYXQiOjE2MDkwNDM1NDYsImV4cCI6MTYwOTA0NzE0Nn0.dwBfYOzG_4MXj48Zn5Nmc3FjB0OuVYyNzGqFLu52syY"
}
```

- 이메일과 패스워드를 이용하여 요청 시 access token을 응답하는 기능을 구현하기
- `AuthAcceptanceTest`을 만족하도록 구현하면 됨
- `AuthAcceptanceTest`에서 제시하는 예외 케이스도 함께 고려하여 구현하기

#### Bearer Auth 유효하지 않은 토큰 인수 테스트

- 유효하지 않은 토큰으로 `/members/me` 요청을 보낼 경우에 대한 예외 처리

### 내 정보 조회 기능

#### 인수 테스트

- MemberAcceptanceTest 클래스의 manageMyInfo메서드에 인수 테스트를 추가하기
- 내 정보 조회, 수정, 삭제 기능을 /members/me 라는 URI 요청으로 동작하도록 검증
- 로그인 후 발급 받은 토큰을 포함해서 요청 하기

```java
@DisplayName("나의 정보를 관리한다.")
@Test
void manageMyInfo() {

}
```

#### 토큰을 통한 인증

- `/members/me` 요청 시 토큰을 확인하여 로그인 정보를 받아올 수 있도록 하기
- `@AuthenticationPrincipal`과 `AuthenticationPrincipalArgumentResolver`을 활용하기
- 아래의 기능이 제대로 동작하도록 구현하기

```java
@GetMapping("/members/me")
public ResponseEntity<MemberResponse> findMemberOfMine(LoginMember loginMember) {
    MemberResponse member = memberService.findMember(loginMember.getId());
    return ResponseEntity.ok().body(member);
}

@PutMapping("/members/me")
public ResponseEntity<MemberResponse> updateMemberOfMine(LoginMember loginMember, @RequestBody MemberRequest param) {
    memberService.updateMember(loginMember.getId(), param);
    return ResponseEntity.ok().build();
}

@DeleteMapping("/members/me")
public ResponseEntity<MemberResponse> deleteMemberOfMine(LoginMember loginMember) {
    memberService.deleteMember(loginMember.getId());
    return ResponseEntity.noContent().build();
}
```

### 즐겨 찾기 기능 구현하기

- 즐겨찾기 기능을 완성하기
- 인증을 포함하여 전체 ATDD 사이클을 경험할 수 있도록 기능을 구현하기

#### 인수 조건

```text
Feature: 즐겨찾기를 관리한다.

  Background
    Given 지하철역 등록되어 있음
    And 지하철 노선 등록되어 있음
    And 지하철 노선에 지하철역 등록되어 있음
    And 회원 등록되어 있음
    And 로그인 되어있음

  Scenario: 즐겨찾기를 관리
    When 즐겨찾기 생성을 요청
    Then 즐겨찾기 생성됨
    When 즐겨찾기 목록 조회 요청
    Then 즐겨찾기 목록 조회됨
    When 즐겨찾기 삭제 요청
    Then 즐겨찾기 삭제됨
  
```

#### 생성 요청/응답

```text
POST /favorites HTTP/1.1
authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBlbWFpbC5jb20iLCJpYXQiOjE2MDkwNDM1NDYsImV4cCI6MTYwOTA0NzE0Nn0.dwBfYOzG_4MXj48Zn5Nmc3FjB0OuVYyNzGqFLu52syY
accept: */*
content-type: application/json; charset=UTF-8
content-length: 27
host: localhost:50336
connection: Keep-Alive
user-agent: Apache-HttpClient/4.5.13 (Java/14.0.2)
accept-encoding: gzip,deflate

{
    "source": "1",
    "target": "3"
}


HTTP/1.1 201 Created
Keep-Alive: timeout=60
Connection: keep-alive
Content-Length: 0
Date: Sun, 27 Dec 2020 04:32:26 GMT
Location: /favorites/1
```

#### 목록 조회 요청/응답

```text
GET /favorites HTTP/1.1
authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBlbWFpbC5jb20iLCJpYXQiOjE2MDkwNDM1NDYsImV4cCI6MTYwOTA0NzE0Nn0.dwBfYOzG_4MXj48Zn5Nmc3FjB0OuVYyNzGqFLu52syY
accept: application/json
host: localhost:50336
connection: Keep-Alive
user-agent: Apache-HttpClient/4.5.13 (Java/14.0.2)
accept-encoding: gzip,deflate

HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sun, 27 Dec 2020 04:32:26 GMT
Keep-Alive: timeout=60
Connection: keep-alive

[
    {
        "id": 1,
        "source": {
            "id": 1,
            "name": "강남역",
            "createdDate": "2020-12-27T13:32:26.364439",
            "modifiedDate": "2020-12-27T13:32:26.364439"
        },
        "target": {
            "id": 3,
            "name": "정자역",
            "createdDate": "2020-12-27T13:32:26.486256",
            "modifiedDate": "2020-12-27T13:32:26.486256"
        }
    }
]
```

#### 삭제 요청/응답

```text
DELETE /favorites/1 HTTP/1.1
authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBlbWFpbC5jb20iLCJpYXQiOjE2MDkwNDM1NDYsImV4cCI6MTYwOTA0NzE0Nn0.dwBfYOzG_4MXj48Zn5Nmc3FjB0OuVYyNzGqFLu52syY
accept: */*
host: localhost:50336
connection: Keep-Alive
user-agent: Apache-HttpClient/4.5.13 (Java/14.0.2)
accept-encoding: gzip,deflate



HTTP/1.1 204 No Content
Keep-Alive: timeout=60
Connection: keep-alive
Date: Sun, 27 Dec 2020 04:32:26 GMT

```

## 힌트

### 인증 기반 인수 테스트

- 사용자 정보를 인수 테스트 메서드의 첫번째 파라미터로 넘겨줄 수 있음

```java
@BeforeEach
public void setUp() {
    ...

    회원_생성을_요청(EMAIL, PASSWORD, 20);
    사용자 = 로그인_되어_있음(EMAIL, PASSWORD);
}

@DisplayName("즐겨찾기를 관리한다.")
@Test
void manageMember() {
    // when
    ExtractableResponse<Response> createResponse = 즐겨찾기_생성을_요청(사용자, 강남역, 정자역);
    ...
}
```

- 참고로 코틀린에서는 확장 함수를 활용하여 작성할 수 있음

```kotlin
val 사용자 = RestAssured.given().log().all().auth().oauth2(accessToken)

@Test
fun 즐겨찾기_관리_기능() {
val response = 사용자.즐겨찾기_생성_요청(강남역, 정자역)
...
} 
```

```kotlin
fun RequestSpecification.즐겨찾기_생성_요청(
source: Long,
target: Long
): ExtractableResponse<FavoriteResponse> {
val favoriteRequest = FavoriteRequest(source, target)

    return this
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(favoriteRequest)
        .`when`().post("/favorites")
        .then().log().all()
        .extract()
}
```

### 프론트엔드

#### 토큰 발급(로그인) 화면

![img image1](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/3c077f854aa646e58b997ea6e3ac7268)

#### 내 정보 관리 화면

![img image2](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/fb39e6eb4b12410abd851a1309ee1a3b)

#### 즐겨찾기 화면

![img image3](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/cd6db9ebf5ed4fdf83708c865047e92e)
