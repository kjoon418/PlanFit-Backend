package success.planfit.schedule.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import success.planfit.course.dto.CourseRequestDto;
import success.planfit.global.validation.NotEmptyAndNotBlank;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class ScheduleRequestDto {

    @NotEmptyAndNotBlank("title")
    private final String title;

    @NotEmptyAndNotBlank("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate date;

    @NotEmptyAndNotBlank("startTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private final LocalTime startTime;

    private final String content;

    @NotEmptyAndNotBlank("location")
    private final CourseRequestDto course;

}
