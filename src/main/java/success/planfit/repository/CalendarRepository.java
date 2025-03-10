package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import success.planfit.domain.course.Calendar;


public interface CalendarRepository extends JpaRepository<Calendar, Long> {
}
