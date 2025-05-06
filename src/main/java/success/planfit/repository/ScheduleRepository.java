package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import success.planfit.entity.schedule.Schedule;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findByShareSerial(String shareSerial);

    @Query("select s from Schedule s" +
            " join fetch s.course c" +
            " join fetch c.spaces sp" +
            " where s.id = :scheduleId")
    Optional<Schedule> findIdWithCourseAndSpace(Long scheduleId);
}
