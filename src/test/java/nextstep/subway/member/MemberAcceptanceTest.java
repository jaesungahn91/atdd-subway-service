package nextstep.subway.member;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.acceptance.AuthAcceptanceTest;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.member.dto.MemberRequest;
import nextstep.subway.member.dto.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("회원 관련 인수테스트")
public class MemberAcceptanceTest extends AcceptanceTest {
    public static final String EMAIL = "email@email.com";
    public static final String PASSWORD = "password";
    public static final String NEW_EMAIL = "newemail@email.com";
    public static final String NEW_PASSWORD = "newpassword";
    public static final int AGE = 20;
    public static final int NEW_AGE = 21;

    @DisplayName("회원 정보를 관리한다.")
    @Test
    void manageMember() {
        // when
        ExtractableResponse<Response> createResponse = 회원_생성을_요청(EMAIL, PASSWORD, AGE);
        // then
        회원_생성됨(createResponse);

        // when
        ExtractableResponse<Response> findResponse = 회원_정보_조회_요청(createResponse);
        // then
        회원_정보_조회됨(findResponse, EMAIL, AGE);

        // when
        ExtractableResponse<Response> updateResponse = 회원_정보_수정_요청(createResponse, NEW_EMAIL, NEW_PASSWORD, NEW_AGE);
        // then
        회원_정보_수정됨(updateResponse);

        // when
        ExtractableResponse<Response> deleteResponse = 회원_삭제_요청(createResponse);
        // then
        회원_삭제됨(deleteResponse);
    }

    /**
     * Feature: 나의정보 관리 기능
     *
     *   Scenario: 나의 정보를 관리한다.
     *     Given 회원등록 됨
     *     Given 로그인 됨(엑세스토큰 발급)
     *     When 나의정보를 요청하면
     *     Then 나의정보가 응답됨
     *     When 나의정보 수정하면
     *     Then 나의정보가 수정됨     
     *     When 나의정보 삭제하면
     *     Then 나의정보(회원)가 삭제됨
     * */
    @DisplayName("나의 정보를 관리한다.")
    @TestFactory
    Stream<DynamicTest> manageMyInfo() {
        //given
        회원_생성을_요청(EMAIL, PASSWORD, AGE);
        TokenResponse tokenResponse = AuthAcceptanceTest.로그인_요청(new TokenRequest(EMAIL, PASSWORD)).as(TokenResponse.class);

        return Stream.of(
            dynamicTest("나의정보를 조회한다.", () -> {
                //when
                MemberResponse response = 나의정보_요청(tokenResponse.getAccessToken()).as(MemberResponse.class);

                //then
                나의정보_조회됨(response);

            }),

            dynamicTest("나의정보를 수정한다.", () -> {
                
                // when
                ExtractableResponse<Response> updateResponse = 나의정보_수정_요청(tokenResponse.getAccessToken(), new MemberRequest(NEW_EMAIL, NEW_PASSWORD, 10));
                
                // then
                나의_정보_수정됨(updateResponse);
                
            }),

            dynamicTest("나의정보(회원)을 삭제한다.", () -> {

                // when
                ExtractableResponse<Response> deleteResponse = 나의정보_삭제_요청(tokenResponse.getAccessToken());

                // then
                나의_정보_삭제됨(deleteResponse);

            })
        );
        
    }
    
    public static ExtractableResponse<Response> 나의정보_요청(String accessToken){
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(accessToken)
                .when().get("/members/me")
                .then().log().all()
                .extract();
    }
    
    public static ExtractableResponse<Response> 나의정보_수정_요청(String accessToken, MemberRequest memberRequest){
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(accessToken)
                .body(memberRequest)
                .when().put("/members/me")
                .then().log().all()
                .extract();
    }
    
    public static ExtractableResponse<Response> 나의정보_삭제_요청(String accessToken){
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(accessToken)
                .when().delete("/members/me")
                .then().log().all()
                .extract();
    }


    public static void 회원_등록_되어있음(String email, String password, int age) {
        회원_생성을_요청(email, password, age);

    }

    public static ExtractableResponse<Response> 회원_생성을_요청(String email, String password, Integer age) {
        MemberRequest memberRequest = new MemberRequest(email, password, age);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberRequest)
                .when().post("/members")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_정보_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_정보_수정_요청(ExtractableResponse<Response> response, String email, String password, Integer age) {
        String uri = response.header("Location");
        MemberRequest memberRequest = new MemberRequest(email, password, age);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberRequest)
                .when().put(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_삭제_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");
        return RestAssured
                .given().log().all()
                .when().delete(uri)
                .then().log().all()
                .extract();
    }

    public static void 회원_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 회원_정보_조회됨(ExtractableResponse<Response> response, String email, int age) {
        MemberResponse memberResponse = response.as(MemberResponse.class);
        assertThat(memberResponse.getId()).isNotNull();
        assertThat(memberResponse.getEmail()).isEqualTo(email);
        assertThat(memberResponse.getAge()).isEqualTo(age);
    }

    public static void 회원_정보_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 회원_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
    
    private void 나의정보_조회됨(MemberResponse response) {
        assertAll(
                ()-> assertThat(response.getId()).isNotNull(),
                ()-> assertThat(response.getEmail()).isEqualTo(EMAIL),
                ()-> assertThat(response.getAge()).isEqualTo(AGE)
        );
    }

    private void 나의_정보_수정됨(ExtractableResponse<Response> updateResponse) {
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
    
    private void 나의_정보_삭제됨(ExtractableResponse<Response> deleteResponse) {
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
