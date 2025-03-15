package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import success.planfit.entity.post.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.user.id  = :userId order by p.createdAt desc")
    Optional<List<Post>> findbyUserIdOrderByCreatedAtDesc(Long userId);

//    @Query("select p from CoursePost p where p.user.id  = :userId")
//    Optional<List<CoursePost>> findbyUserId(Long userId);



}
