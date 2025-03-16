package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import success.planfit.entity.space.SpaceDetail;

import java.util.Optional;

public interface SpaceDetailRepository extends JpaRepository<SpaceDetail, Long> {

    Optional<SpaceDetail> findByGooglePlacesIdentifier(String googlePlacesIdentifier);

}
