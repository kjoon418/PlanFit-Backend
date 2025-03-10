package success.planfit.dto.request;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
public class CoursePostsSaveByCalendarRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String title;
    private byte[] titlePhoto;
    private String location;
}
