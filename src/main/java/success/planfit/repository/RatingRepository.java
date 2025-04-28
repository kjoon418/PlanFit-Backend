package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import success.planfit.entity.rating.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
