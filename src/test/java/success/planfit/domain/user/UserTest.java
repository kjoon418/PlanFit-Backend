package success.planfit.domain.user;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional
class UserTest {

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("유저 생성: 구글")
    void createUser_google() throws Exception {
        // given
        GoogleUser user = GoogleUser.builder()
                .googleIdentifier("helloGoogle")
                .name("googleUserA")
                .phoneNumber("01011112222")
                .identity(IdentityType.STUDENT)
                .email("googleUser@gmail.com")
//                .profilePhoto("helloProfileUrl") 이미지 URL 값을 가져올 수 없어 테스트 보류
                .build();

        // when
        em.persist(user);
        User findUser = em.find(User.class, user.getId());

        // then
        Assertions.assertThat(findUser).isEqualTo(user);
    }

    @Test
    @DisplayName("유저 생성: 카카오")
    void createUser_kakao() throws Exception {
        // given
        KakaoUser user = KakaoUser.builder()
                .kakaoIdentifier(123L)
                .name("kakaoUserA")
                .phoneNumber("01011112222")
                .birthOfDate(LocalDate.now())
                .identity(IdentityType.STUDENT)
                .email("kakaoUser@kakao.com")
//                .profilePhoto("helloProfileUrl") 이미지 URL 값을 가져올 수 없어 테스트 보류
                .build();

        // when
        em.persist(user);
        User findUser = em.find(User.class, user.getId());

        // then
        Assertions.assertThat(findUser).isEqualTo(user);
    }

    @Test
    @DisplayName("유저 생성: 플랜핏")
    void createUser_planFit() throws Exception {
        // given
        PlanfitUser user = PlanfitUser.builder()
                .loginId("helloId")
                .password("helloPassword")
                .name("planFitUserA")
                .phoneNumber("01011112222")
                .birthOfDate(LocalDate.now())
                .identity(IdentityType.STUDENT)
                .email("planFitUser@planfit.com")
//                .profilePhoto("helloProfileUrl") 이미지 URL 값을 가져올 수 없어 테스트 보류
                .build();

        // when
        em.persist(user);
        User findUser = em.find(User.class, user.getId());

        // then
        Assertions.assertThat(findUser).isEqualTo(user);
    }
}