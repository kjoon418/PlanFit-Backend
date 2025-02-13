package success.planfit.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.domain.user.IdentityType;
import success.planfit.domain.user.PlanfitUser;
import success.planfit.domain.user.User;
import success.planfit.dto.request.PlanfitUserSignInRequestDto;
import success.planfit.dto.request.PlanfitUserSignUpRequestDto;
import success.planfit.dto.response.TokenResponseDto;
import success.planfit.exception.IllegalRequestException;
import success.planfit.jwt.TokenType;
import success.planfit.photo.PhotoProvider;
import success.planfit.photo.PhotoType;
import success.planfit.utils.TestUtil;
import success.planfit.utils.UserInfo;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Component
@SpringBootTest
@Transactional
class AuthorizationServiceTest {

    private static final String BASIC_NAME = "helloName";
    private static final String BASIC_LOGIN_ID = "helloId";
    private static final String BASIC_PASSWORD = "helloPassword1234!";
    private static final String BASIC_PHONE_NUMBER = "010-1234-5678";
    private static final LocalDate BASIC_BIRTH_OF_DATE = LocalDate.now();
    private static final IdentityType BASIC_IDENTITY = IdentityType.STUDENT;
    private static final String BASIC_EMAIL = "hello@email.com";
    private String BASIC_PROFILE_PHOTO;

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private EntityManager em;
    @Autowired
    private TestUtil util;

    @BeforeEach
    void beforeEach() {
        BASIC_PROFILE_PHOTO = util.getEncodedImage();
    }

    @AfterEach
    void afterEach() {
        util.clearEntityManager(em);
    }

    @Nested
    @DisplayName("회원가입")
    class SignUp {

        @Test
        @DisplayName("회원가입 성공(플랜핏 회원)")
        void planfit() throws Exception {
            // given: 플랜핏 회원가입 DTO 생성
            PlanfitUserSignUpRequestDto requestDto = PlanfitUserSignUpRequestDto.builder()
                    .name(BASIC_NAME)
                    .loginId(BASIC_LOGIN_ID)
                    .password(BASIC_PASSWORD)
                    .phoneNumber(BASIC_PHONE_NUMBER)
                    .birthOfDate(BASIC_BIRTH_OF_DATE)
                    .identity(BASIC_IDENTITY)
                    .email(BASIC_EMAIL)
                    .profilePhoto(BASIC_PROFILE_PHOTO)
                    .photoType(PhotoType.ENCODED_BINARY)
                    .build();

            // when: DTO를 통해 회원가입
            String accessToken = authorizationService.planfitSignUp(requestDto)
                    .accessToken();
            long userId = util.getUserIdFromJwt(accessToken);
            util.clearEntityManager(em);

            // then: 회원이 제대로 저장되었는지 확인
            PlanfitUser user = em.find(PlanfitUser.class, userId);
            assertUserInfoIsBasic(user);
        }
    }

    @Nested
    @DisplayName("로그인")
    class SignIn {

        @Test
        @DisplayName("로그인 성공(플랜핏 회원)")
        void planfit() throws Exception {
            // given: 플랜핏 회원가입
            long userId = util.signUpPlanfitUser()
                    .userId();
            util.clearEntityManager(em);

            // when: 로그인 요청
            TokenResponseDto responseDto = authorizationService.planfitSignIn(PlanfitUserSignInRequestDto.builder()
                    .loginId(BASIC_LOGIN_ID)
                    .password(BASIC_PASSWORD)
                    .build()
            );

            // then: 정상적인 Response DTO가 반환된 것을 검증하고, 저장된 RefreshToken과 방급 발급한 토큰이 일치하는지 확인
            long accessTokenUserId = util.getUserIdFromJwt(responseDto.accessToken());
            long refreshTokenUserId = util.getUserIdFromJwt(responseDto.refreshToken());
            assertThat(accessTokenUserId).isEqualTo(userId);
            assertThat(refreshTokenUserId).isEqualTo(userId);

            PlanfitUser findUser = em.find(PlanfitUser.class, userId);
            assertThat(findUser.getRefreshToken().getTokenValue()).isEqualTo(responseDto.refreshToken());
        }
    }

    @Nested
    @DisplayName("엑세스 토큰 재발급")
    class ReissueAccessToken {

        @Test
        @DisplayName("재발급 성공(플랜핏 회원)")
        void planfit() throws Exception {
            // given: 플랜핏 회원가입
            String refreshToken = util.signUpPlanfitUser()
                    .refreshToken();
            util.clearEntityManager(em);

            // when: 엑세스 토큰 재발급
            String accessToken = authorizationService.reissueAccessToken(refreshToken)
                    .accessToken();

            // then: 정상적인 값을 반환했는지 검증
            assertThat(util.validateToken(accessToken, TokenType.ACCESS)).isTrue();
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {

        @Test
        @DisplayName("플랜핏")
        void planfit() throws Exception {
            // given: 플랜핏 회원가입
            UserInfo userInfo = util.signUpPlanfitUser();
            util.clearEntityManager(em);

            // when: 리프레쉬 토큰 무효화
            authorizationService.invalidateRefreshToken(userInfo.userId());
            util.clearEntityManager(em);

            // then: 리프레쉬 토큰이 null 값이 된 것을 확인하고, 엑세스 토큰 요청 재발급이 실패하는 것 확인
            PlanfitUser planfitUser = em.find(PlanfitUser.class, userInfo.userId());
            assertThat(planfitUser.getRefreshToken().getTokenValue()).isNull();

            assertThatThrownBy(() -> authorizationService.reissueAccessToken(userInfo.refreshToken()))
                    .isInstanceOf(IllegalRequestException.class);
        }

    }

    @Nested
    @DisplayName("회원 탈퇴")
    class Withdraw {

        @Test
        @DisplayName("플랜핏")
        void planfit() throws Exception {
            // given: 플랜핏 회원가입
            long userId = util.signUpPlanfitUser()
                    .userId();
            util.clearEntityManager(em);

            // when: 회원 탈퇴
            authorizationService.deleteUser(userId);
            util.clearEntityManager(em);

            // then: 조회 실패
            PlanfitUser findUser = em.find(PlanfitUser.class, userId);
            assertThat(findUser).isNull();
        }

    }

    /**
     * 회원의 모든 정보가 BASIC 상수값과 일치하는지 검증하는 메서드.
     *
     * @param user 검증할 회원
     */
    private void assertUserInfoIsBasic(User user) {
        assertThat(user.getName()).isEqualTo(BASIC_NAME);
        assertThat(user.getPhoneNumber()).isEqualTo(BASIC_PHONE_NUMBER);
        assertThat(user.getBirthOfDate()).isEqualTo(BASIC_BIRTH_OF_DATE);
        assertThat(user.getIdentity()).isEqualTo(BASIC_IDENTITY);
        assertThat(user.getEmail()).isEqualTo(BASIC_EMAIL);
        assertThat(user.getProfilePhoto()).isEqualTo(PhotoProvider.decode(BASIC_PROFILE_PHOTO));

        if (user instanceof PlanfitUser planfitUser) {
            assertThat(planfitUser.getLoginId()).isEqualTo(BASIC_LOGIN_ID);
            assertThat(planfitUser.getPassword()).isEqualTo(BASIC_PASSWORD);
        }
    }

}