package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import success.planfit.entity.like.CommentLike;
import success.planfit.entity.like.PostLike;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId);

    void deleteAllByCommentId(Long commentId);
    void deleteAllByCommentIdIn(List<Long> commentIds);
}
