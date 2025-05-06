package success.planfit.schedule.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import success.planfit.course.dto.CourseResponseDto;
import success.planfit.entity.schedule.Schedule;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleResponseDto {

    private final Long scheduleId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate date;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private final LocalTime startTime;
    private final String title;
    private final String content;
    private final Integer currentSequence;

    private final CourseResponseDto course;

    public static ScheduleResponseDto from(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .scheduleId(schedule.getId())
                .date(schedule.getDate())
                .startTime(schedule.getStartTime())
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .currentSequence(schedule.getCurrentSequence())
                .course(CourseResponseDto.from(schedule.getCourse()))
                .build();
    }

}
