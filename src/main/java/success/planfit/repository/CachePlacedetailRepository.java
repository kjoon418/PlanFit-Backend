package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import success.planfit.domain.CachePlacedetail;

import java.util.Optional;


public interface CachePlacedetailRepository extends JpaRepository<CachePlacedetail, Long> {
    @Query("select p from CachePlacedetail p where p.googlePlacesIdentifier = :placeId")
    Optional<CachePlacedetail> findByGooglePlacesIdentifier(@Param("placeId") String placeId);
}