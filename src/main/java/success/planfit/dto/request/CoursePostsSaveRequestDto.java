package success.planfit.dto.request;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CoursePostsSaveRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String title;
    private byte[] titlePhoto;
    private String location;
    private List<TimetableCreationRequestDto> timetable = new ArrayList<TimetableCreationRequestDto>();
}
