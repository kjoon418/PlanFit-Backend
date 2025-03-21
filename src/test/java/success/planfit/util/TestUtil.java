package success.planfit.util;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
public class TestUtil {

    public void clearEntityManager(EntityManager entityManager) {
        entityManager.flush();
        entityManager.clear();
    }

}
