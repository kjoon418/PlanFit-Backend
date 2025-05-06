package success.planfit.schedule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import success.planfit.global.validation.NotEmptyAndNotBlank;

@Getter
@Builder
@AllArgsConstructor
public class ScheduleCurrentSequenceUpdateRequestDto {

    @NotEmptyAndNotBlank("scheduleId")
    private final long scheduleId;

    @NotEmptyAndNotBlank("scheduleId")
    private final int sequence;

}
