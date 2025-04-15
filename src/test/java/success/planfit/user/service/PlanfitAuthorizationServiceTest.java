package success.planfit.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.entity.user.IdentityType;
import success.planfit.entity.user.PlanfitUser;
import success.planfit.entity.user.User;
import success.planfit.fixture.UserFixture;
import success.planfit.global.jwt.TokenProvider;
import success.planfit.global.jwt.TokenType;
import success.planfit.global.photo.PhotoType;
import success.planfit.repository.UserRepository;
import success.planfit.user.dto.PlanfitUserSignInRequestDto;
import success.planfit.user.dto.PlanfitUserSignUpRequestDto;
import success.planfit.util.EqualityChecker;
import success.planfit.util.TestUtil;

import java.time.LocalDate;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@RequiredArgsConstructor
class PlanfitAuthorizationServiceTest {

    private static final String DEFAULT_NAME = "DEFAULT NAME";
    private static final String DEFAULT_LOGIN_ID = "DEFAULT_LOGIN_ID";
    private static final String DEFAULT_PASSWORD = "DEFAULT_PASSWORD";
    private static final String DEFAULT_EMAIL = "DEFAILT@EMAIL.com";
    private static final String DEFAULT_PHONE_NUMBER = "01012345678";
    private static final LocalDate DEFAULT_BIRTH_OF_DATE = LocalDate.of(2001, 4, 18);
    private static final IdentityType DEFAULT_IDENTITY = IdentityType.STUDENT;
    private static final String DEFAULT_PROFILE_PHOTO = null;
    private static final PhotoType DEFAULT_PHOTO_TYPE = PhotoType.NULL;
    private static final String NOT_EXIST_LOGIN_ID = "NOT_EXIST_LOGIN_ID";
    private static final String NOT_EXIST_PASSWORD = "NOT_EXIST_PASSWORD";

    private static final Supplier<RuntimeException> TEST_FAIL = () -> new RuntimeException("테스트 실패");

    @Autowired
    private EqualityChecker equalityChecker;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private TestUtil util;
    @Autowired
    private EntityManager em;

    @Autowired
    private PlanfitAuthorizationService planfitAuthorizationService;
    @Autowired
    private UserRepository userRepository;

    private PlanfitUser existUser;

    @BeforeEach
    void initialize() {
        PlanfitUser user = UserFixture.USER_A.createInstance();

        this.existUser = userRepository.save(user);
    }

    @AfterEach
    void clearEntityManager() {
        util.clearEntityManager(em);
    }

    @Nested
    class planfitSignUp {

        @Test
        @DisplayName("DTO의 내용을 통해 새로운 PlanfitUser 엔티티를 생성한다")
        void createNewPlanfitUser() {
            PlanfitUserSignUpRequestDto requestDto = createRequestDto();

            planfitAuthorizationService.planfitSignUp(requestDto);

            PlanfitUser user = findUserByRequestDto(requestDto);

            equalityChecker.check(user, requestDto);
        }

        @Test
        @DisplayName("새로 만들어진 회원에 대한 Access 토큰을 반환한다")
        void returnAccessToken() {
            PlanfitUserSignUpRequestDto requestDto = createRequestDto();

            String accessToken = planfitAuthorizationService.planfitSignUp(requestDto)
                    .accessToken();
            PlanfitUser user = findUserByRequestDto(requestDto);

            assertThat(isValidToken(user, accessToken, TokenType.ACCESS)).isTrue();
        }

        @Test
        @DisplayName("새로 만들어진 회원에 대한 Refresh 토큰을 반환한다")
        void returnRefreshToken() {
            PlanfitUserSignUpRequestDto requestDto = createRequestDto();

            String refreshToken = planfitAuthorizationService.planfitSignUp(requestDto)
                    .refreshToken();
            PlanfitUser user = findUserByRequestDto(requestDto);

            assertThat(isValidToken(user, refreshToken, TokenType.REFRESH)).isTrue();
        }

        @Test
        @DisplayName("해당 회원에 새로운 Refresh 토큰을 등록하고, 그 값과 똑같은 Refresh 토큰을 반환한다")
        void returnRefreshTokenEqualToUser() {
            PlanfitUserSignUpRequestDto requestDto = createRequestDto();

            String returnedRefreshToken = planfitAuthorizationService.planfitSignUp(requestDto)
                    .refreshToken();
            PlanfitUser user = findUserByRequestDto(requestDto);

            String refreshTokenOfUser = user.getRefreshToken()
                    .getTokenValue();
            assertThat(returnedRefreshToken).isEqualTo(refreshTokenOfUser);
        }

        @Test
        @DisplayName("이미 존재하는 login id를 사용하려 할 경우 예외가 발생한다")
        void ifUseExistLoginIdThenThrowException() {
            PlanfitUserSignUpRequestDto requestDtoWithExistLoginId = createRequestDtoFrom(existUser.getLoginId());

            assertThatThrownBy(() -> planfitAuthorizationService.planfitSignUp(requestDtoWithExistLoginId))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }

        private PlanfitUserSignUpRequestDto createRequestDto() {
            return PlanfitUserSignUpRequestDto.builder()
                    .name(DEFAULT_NAME)
                    .loginId(DEFAULT_LOGIN_ID)
                    .password(DEFAULT_PASSWORD)
                    .email(DEFAULT_EMAIL)
                    .phoneNumber(DEFAULT_PHONE_NUMBER)
                    .birthOfDate(DEFAULT_BIRTH_OF_DATE)
                    .identity(DEFAULT_IDENTITY)
                    .profilePhoto(DEFAULT_PROFILE_PHOTO)
                    .photoType(DEFAULT_PHOTO_TYPE)
                    .build();
        }

        private PlanfitUserSignUpRequestDto createRequestDtoFrom(String loginId) {
            return PlanfitUserSignUpRequestDto.builder()
                    .name(DEFAULT_NAME)
                    .loginId(loginId)
                    .password(DEFAULT_PASSWORD)
                    .email(DEFAULT_EMAIL)
                    .phoneNumber(DEFAULT_PHONE_NUMBER)
                    .birthOfDate(DEFAULT_BIRTH_OF_DATE)
                    .identity(DEFAULT_IDENTITY)
                    .profilePhoto(DEFAULT_PROFILE_PHOTO)
                    .photoType(DEFAULT_PHOTO_TYPE)
                    .build();
        }

        private PlanfitUser findUserByRequestDto(PlanfitUserSignUpRequestDto requestDto) {
            return userRepository.findByLoginId(requestDto.getLoginId())
                    .orElseThrow(TEST_FAIL);
        }

    }

    @Nested
    class planfitSignIn {

        @Test
        @DisplayName("올바른 아이디와 비밀번호를 전달하면 해당 유저에 대한 Access 토큰을 반환한다")
        void ifUseCorrectIdAndPasswordThenReturnAccessToken() {
            PlanfitUserSignInRequestDto requestDto = createRequestDtoFrom(existUser);

            String accessToken = planfitAuthorizationService.planfitSignIn(requestDto)
                    .accessToken();

            assertThat(isValidToken(existUser, accessToken, TokenType.ACCESS)).isTrue();
        }

        @Test
        @DisplayName("올바른 아이디와 비밀번호를 전달하면 해당 유저에 대한 Refresh 토큰을 반환한다")
        void ifUseCorrectIdAndPasswordThenReturnRefreshToken() {
            PlanfitUserSignInRequestDto requestDto = createRequestDtoFrom(existUser);

            String refreshToken = planfitAuthorizationService.planfitSignIn(requestDto)
                    .refreshToken();

            assertThat(isValidToken(existUser, refreshToken, TokenType.REFRESH)).isTrue();
        }

        @Test
        @DisplayName("로그인에 성공하면 해당 회원에 새로운 Refresh 토큰을 등록하고, 그 값과 똑같은 Refresh 토큰을 반환한다")
        void returnRefreshTokenEqualToUser() {
            PlanfitUserSignInRequestDto requestDto = createRequestDtoFrom(existUser);

            String returnedRefreshToken = planfitAuthorizationService.planfitSignIn(requestDto)
                    .refreshToken();

            String refreshTokenOfUser = existUser.getRefreshToken()
                    .getTokenValue();
            assertThat(returnedRefreshToken).isEqualTo(refreshTokenOfUser);
        }

        @Test
        @DisplayName("존재하지 않는 login id를 전달하면 예외가 발생한다")
        void ifUseNotExistLoginIdThenThrowException() {
            PlanfitUserSignInRequestDto illegalRequestDto = PlanfitUserSignInRequestDto.builder()
                    .loginId(NOT_EXIST_LOGIN_ID)
                    .password(existUser.getPassword())
                    .build();

            assertThatThrownBy(() -> planfitAuthorizationService.planfitSignIn(illegalRequestDto))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 password를 전달하면 예외가 발생한다")
        void ifUseNotExistPasswordThenThrowException() {
            PlanfitUserSignInRequestDto illegalRequestDto = PlanfitUserSignInRequestDto.builder()
                    .loginId(existUser.getLoginId())
                    .password(NOT_EXIST_PASSWORD)
                    .build();

            assertThatThrownBy(() -> planfitAuthorizationService.planfitSignIn(illegalRequestDto))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        private PlanfitUserSignInRequestDto createRequestDtoFrom(PlanfitUser planfitUser) {
            return PlanfitUserSignInRequestDto.builder()
                    .loginId(planfitUser.getLoginId())
                    .password(planfitUser.getPassword())
                    .build();
        }

    }

    @Nested
    class isDuplicatedLoginId {

        @Test
        @DisplayName("이미 존재하는 login id일 경우 true를 반환한다")
        void ifExistLoginIdThenReturnTrue() {
            boolean result = planfitAuthorizationService.isDuplicatedLoginId(existUser.getLoginId());

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 login id일 경우 false를 반환한다")
        void ifNotExistLoginIdThenReturnFalse() {
            boolean result = planfitAuthorizationService.isDuplicatedLoginId(NOT_EXIST_LOGIN_ID);

            assertThat(result).isFalse();
        }

    }

    private boolean isValidToken(User user, String token, TokenType tokenType) {
        if (!tokenProvider.validateToken(token, tokenType)) {
            return false;
        }

        long userIdFromToken = getUserIdFromToken(token);

        return user.getId().equals(userIdFromToken);
    }

    private long getUserIdFromToken(String token) {
        String userId = tokenProvider.parseClaims(token)
                .getSubject();

        return Long.parseLong(userId);
    }

}
