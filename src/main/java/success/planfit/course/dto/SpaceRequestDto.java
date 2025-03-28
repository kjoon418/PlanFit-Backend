package success.planfit.course.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import success.planfit.global.validation.NotEmptyAndNotBlank;

@Getter
@AllArgsConstructor
public class SpaceRequestDto {

    @NotEmptyAndNotBlank("googlePlacesIdentifier")
    private final String googlePlacesIdentifier;

    private int sequence;
}
