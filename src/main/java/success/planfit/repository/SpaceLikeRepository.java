package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import success.planfit.entity.like.SpaceLike;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.user.User;

import java.util.Optional;

public interface SpaceLikeRepository extends JpaRepository<SpaceLike, Long> {
    boolean existsByUserAndSpaceDetail(User user, SpaceDetail spaceDetail);
    Optional<SpaceLike> findByUserAndSpaceDetail(User user, SpaceDetail spaceDetail);
}