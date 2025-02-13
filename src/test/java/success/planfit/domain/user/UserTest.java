package success.planfit.domain.user;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.photo.PhotoProvider;
import success.planfit.utils.TestUtil;

import java.time.LocalDate;

@SpringBootTest
@Transactional
class UserTest {

    private static final String BASIC_GOOGLE_IDENTIFIER = "helloGoogleIdentifier";
    private static final Long BASIC_KAKAO_IDENTIFIER = 123123123L;
    private static final String BASIC_NAME = "helloName";
    private static final String BASIC_LOGIN_ID = "helloId";
    private static final String BASIC_PASSWORD = "helloPassword1234!";
    private static final String BASIC_PHONE_NUMBER = "010-1234-5678";
    private static final LocalDate BASIC_BIRTH_OF_DATE = LocalDate.now();
    private static final IdentityType BASIC_IDENTITY = IdentityType.STUDENT;
    private static final String BASIC_EMAIL = "hello@email.com";
    private byte[] BASIC_PROFILE_PHOTO;

    @Autowired
    private EntityManager em;
    @Autowired
    private TestUtil util;

    @BeforeEach
    void beforeEach() {
        BASIC_PROFILE_PHOTO = util.getBinaryImage();
    }

    @AfterEach
    void afterEach() {
        util.clearEntityManager(em);
    }

    @Nested
    @DisplayName("유저 생성")
    class CreateUser {

        @Test
        @DisplayName("구글")
        void google() throws Exception {
            // given
            GoogleUser user = GoogleUser.builder()
                    .googleIdentifier(BASIC_GOOGLE_IDENTIFIER)
                    .name(BASIC_NAME)
                    .phoneNumber(BASIC_PHONE_NUMBER)
                    .birthOfDate(BASIC_BIRTH_OF_DATE)
                    .identity(BASIC_IDENTITY)
                    .email(BASIC_EMAIL)
                    .profilePhoto(BASIC_PROFILE_PHOTO)
                    .build();

            // when
            em.persist(user);
            User findUser = em.find(GoogleUser.class, user.getId());

            // then
            Assertions.assertThat(findUser).isEqualTo(user);
        }

        @Test
        @DisplayName("카카오")
        void kakao() throws Exception {
            // given
            KakaoUser user = KakaoUser.builder()
                    .kakaoIdentifier(BASIC_KAKAO_IDENTIFIER)
                    .name(BASIC_NAME)
                    .phoneNumber(BASIC_PHONE_NUMBER)
                    .birthOfDate(BASIC_BIRTH_OF_DATE)
                    .identity(BASIC_IDENTITY)
                    .email(BASIC_EMAIL)
                    .profilePhoto(BASIC_PROFILE_PHOTO)
                    .build();

            // when
            em.persist(user);
            User findUser = em.find(KakaoUser.class, user.getId());

            // then
            Assertions.assertThat(findUser).isEqualTo(user);
        }

        @Test
        @DisplayName("플랜핏")
        void planfit() throws Exception {
            // given
            PlanfitUser user = PlanfitUser.builder()
                    .loginId(BASIC_LOGIN_ID)
                    .password(BASIC_PASSWORD)
                    .name(BASIC_NAME)
                    .phoneNumber(BASIC_PHONE_NUMBER)
                    .birthOfDate(BASIC_BIRTH_OF_DATE)
                    .identity(BASIC_IDENTITY)
                    .email(BASIC_EMAIL)
                    .profilePhoto(BASIC_PROFILE_PHOTO)
                    .build();

            // when
            em.persist(user);
            User findUser = em.find(PlanfitUser.class, user.getId());

            // then
            Assertions.assertThat(findUser).isEqualTo(user);
        }

    }

    @Nested
    @DisplayName("유저 삭제")
    class DeleteUser {

        @Test
        @DisplayName("구글")
        void google() throws Exception {
            // given
            GoogleUser user = GoogleUser.builder()
                    .googleIdentifier("helloGoogle")
                    .name("googleUserA")
                    .phoneNumber("01011112222")
                    .identity(IdentityType.STUDENT)
                    .email("googleUser@gmail.com")
//                    .profilePhoto("helloProfileUrl") 이미지 바이너리 값을 가져올 수 없어 테스트 보류
                    .build();

            // when
            em.persist(user);
            util.clearEntityManager(em);

            // then
            User findUser = em.find(User.class, user.getId());
            em.remove(findUser);
        }

        @Test
        @DisplayName("카카오")
        void kakao() throws Exception {
            // given
            KakaoUser user = KakaoUser.builder()
                    .kakaoIdentifier(123L)
                    .name("kakaoUserA")
                    .phoneNumber("01011112222")
                    .birthOfDate(LocalDate.now())
                    .identity(IdentityType.STUDENT)
                    .email("kakaoUser@kakao.com")
//                    .profilePhoto("helloProfileUrl") 이미지 바이너리 값을 가져올 수 없어 테스트 보류
                    .build();

            // when
            em.persist(user);
            util.clearEntityManager(em);

            // then
            User findUser = em.find(User.class, user.getId());
            em.remove(findUser);
        }

        @Test
        @DisplayName("플랜핏")
        void planfit() throws Exception {
            // given
            PlanfitUser user = PlanfitUser.builder()
                    .loginId("helloId")
                    .password("helloPassword")
                    .name("planFitUserA")
                    .phoneNumber("01011112222")
                    .birthOfDate(LocalDate.now())
                    .identity(IdentityType.STUDENT)
                    .email("planFitUser@planfit.com")
//                    .profilePhoto("helloProfileUrl") 이미지 바이너리 값을 가져올 수 없어 테스트 보류
                    .build();

            // when
            em.persist(user);
            util.clearEntityManager(em);

            // then
            User findUser = em.find(User.class, user.getId());
            em.remove(findUser);
        }

    }

}