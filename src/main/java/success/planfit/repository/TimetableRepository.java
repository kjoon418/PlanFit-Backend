package success.planfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import success.planfit.domain.course.Timetable;


public interface TimetableRepository extends JpaRepository<Timetable, Long> {

 }
