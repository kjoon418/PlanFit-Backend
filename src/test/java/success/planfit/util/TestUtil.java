package success.planfit.util;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import success.planfit.entity.course.Course;
import success.planfit.entity.schedule.Schedule;
import success.planfit.entity.space.Space;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TestUtil {

    public void clearEntityManager(EntityManager entityManager) {
        entityManager.flush();
        entityManager.clear();
    }

    public void connectEntities(User user, Schedule schedule, Course course, List<SpaceDetail> spaceDetails) {
        List<Space> spaces = createSpaces(spaceDetails);

        user.addSchedule(schedule);
        schedule.setCourse(course);
        course.addSpaces(spaces);
    }

    private List<Space> createSpaces(List<SpaceDetail> spaceDetails) {
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
