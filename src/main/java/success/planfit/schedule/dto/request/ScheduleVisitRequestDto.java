package success.planfit.schedule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import success.planfit.global.validation.NotEmptyAndNotBlank;

@Getter
@AllArgsConstructor
public class ScheduleVisitRequestDto {

    @NotEmptyAndNotBlank("scheduleId")
    private final long scheduleId;

    @NotEmptyAndNotBlank("scheduleId")
    private final int sequence;

}
