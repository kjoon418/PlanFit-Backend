package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import success.planfit.domain.post.CoursePost;
import success.planfit.domain.post.SpacePost;

import java.util.List;
import java.util.Optional;

public interface CoursePostRepository extends JpaRepository<CoursePost, Long> {

    @Query("select p from CoursePost p where p.user.id  = :userId order by p.createdAt desc")
    Optional<List<CoursePost>> findbyUserIdOrderByCreatedAtDesc(Long userId);

//    @Query("select p from CoursePost p where p.user.id  = :userId")
//    Optional<List<CoursePost>> findbyUserId(Long userId);



}
