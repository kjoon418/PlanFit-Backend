package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import success.planfit.domain.bookmark.CoursePostBookmark;


import java.util.Optional;

public interface CoursePostBookmarkRepository extends JpaRepository<CoursePostBookmark, Long> {
    @Query("select b from CoursePostBookmark b where b.user.id = :userId")
    Optional<CoursePostBookmark> findByUserId(Long userId);
}
