package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import success.planfit.domain.post.SpacePost;

public interface SpacePostRepository extends JpaRepository<SpacePost, Long> {
}
