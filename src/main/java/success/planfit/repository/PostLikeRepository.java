package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import success.planfit.entity.like.PostLike;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);

    @Query("select p from PostLike p where p.user.id  = :userId order by p.id desc")
    List<PostLike> findByUserId(Long userId);

}
