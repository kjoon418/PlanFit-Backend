package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import success.planfit.entity.comment.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
