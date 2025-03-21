package success.planfit.util;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import success.planfit.entity.space.Space;
import success.planfit.entity.space.SpaceDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TestUtil {

    public void clearEntityManager(EntityManager entityManager) {
        entityManager.flush();
        entityManager.clear();
    }

    public List<Space> createSpaces(List<SpaceDetail> spaceDetails) {
        List<Space> spaces = new ArrayList<>();

        for (int sequence = 0; sequence < spaceDetails.size(); sequence++) {
            spaces.add(Space.builder()
                    .spaceDetail(spaceDetails.get(sequence))
                    .sequence(sequence)
                    .build()
            );
        }

        return Collections.unmodifiableList(spaces);
    }

}
