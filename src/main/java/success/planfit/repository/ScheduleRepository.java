package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import success.planfit.entity.schedule.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
