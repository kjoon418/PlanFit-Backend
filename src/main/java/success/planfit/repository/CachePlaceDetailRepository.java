package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import success.planfit.domain.CachePlaceDetail;

import java.util.Optional;


public interface CachePlaceDetailRepository extends JpaRepository<CachePlaceDetail, Long> {

    @Query(value = "select p from CachePlaceDetail p where p.googlePlacesIdentifier = :placeId")
    Optional<CachePlaceDetail> findByGooglePlacesIdentifier(@Param("placeId") String placeId);

}
