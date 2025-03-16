package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import success.planfit.entity.post.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
