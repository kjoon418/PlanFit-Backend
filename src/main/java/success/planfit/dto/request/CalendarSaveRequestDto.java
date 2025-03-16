package success.planfit.dto.request;

import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CalendarSaveRequestDto {

    private String title;
    private LocalDate date;
    private List<TimetableCreationRequestDto> timetable = new ArrayList<TimetableCreationRequestDto>();

}
