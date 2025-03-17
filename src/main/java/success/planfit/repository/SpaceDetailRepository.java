package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import success.planfit.entity.space.SpaceDetail;

import java.util.Optional;


public interface SpaceDetailRepository extends JpaRepository<SpaceDetail, Long> {

    @Query(value = "select p from SpaceDetail p where p.googlePlacesIdentifier = :placeId")
    Optional<SpaceDetail> findByGooglePlacesIdentifier(@Param("placeId") String placeId);

}
