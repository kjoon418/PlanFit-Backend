package success.planfit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import success.planfit.entity.post.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    @Query("select p from Post p order by p.createdAt desc limit :n offset 0")
    Optional<List<Post>> findTopNByOrderByCreatedAtDesc(int n);

    @Query("select p from Post p left join fetch p.comments where p.id = :postId")
    Optional<Post> findByIdWithComment(@Param("postId") Long postId);

    @Query("select p from Post p left join fetch p.user" +
            " left join fetch p.course" +
            " left join fetch p.comments" +
            " where p.id = :postId")
    Optional<Post> findByIdWithUserAndCourseAndComment(@Param("postId") Long postId);

}
