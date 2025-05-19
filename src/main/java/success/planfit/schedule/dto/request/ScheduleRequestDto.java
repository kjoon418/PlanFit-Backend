package success.planfit.schedule.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import success.planfit.course.dto.CourseRequestDto;
import success.planfit.global.validation.NotEmptyAndNotBlank;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class ScheduleRequestDto {

    @NotEmptyAndNotBlank("title")
    private final String title;

    @NotEmptyAndNotBlank("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate date;

    @NotEmptyAndNotBlank("startTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(description = "시작 시간", type = "string", example = "17:50:00")
    private final LocalTime startTime;

    private final String content;

    @NotEmptyAndNotBlank("location")
    private final CourseRequestDto course;

}
