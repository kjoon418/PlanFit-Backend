package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import success.planfit.entity.user.PlanfitUser;
import success.planfit.entity.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from PlanfitUser u where u.loginId = :loginId")
    Optional<PlanfitUser> findByLoginId(@Param("loginId") String loginId);

    @Query("select u from PlanfitUser u where u.loginId = :loginId and u.password = :password")
    Optional<User> findByLoginIdAndPassword(@Param("loginId") String loginId, @Param("password") String password);

    @Query("select u from GoogleUser u where u.googleIdentifier = :identifier")
    Optional<User> findByGoogleIdentifier(@Param("identifier") String identifier);

    @Query("select u from KakaoUser u where u.kakaoIdentifier = :identifier")
    Optional<User> findByKakaoIdentifier(@Param("identifier") Long identifier);

    @Query("select u from User u left join fetch u.posts where u.id = :userId")
    Optional<User> findByIdWithPost(@Param("userId") Long userId);

    @Query("select u from User u left join fetch u.schedules where u.id = :userId")
    Optional<User> findByIdWithSchedule(@Param("userId") Long userId);

}
