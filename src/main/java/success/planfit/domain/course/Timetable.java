package success.planfit.domain.course;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Entity
public class Timetable {

    @Id

    private int id;

    private int calendar_id;

    private LocalTime start_time;

    private LocalTime end_time;

    private String space_name;

    private String location;

    private SpaceType space_tag;

    private String memo;

    private String link;




}
