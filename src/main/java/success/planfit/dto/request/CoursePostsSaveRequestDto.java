package success.planfit.dto.request;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
public class CoursePostsSaveRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String title;
}
