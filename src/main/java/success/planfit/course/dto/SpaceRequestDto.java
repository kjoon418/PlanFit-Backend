package success.planfit.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import success.planfit.global.validation.NotEmptyAndNotBlank;

@Getter
@AllArgsConstructor
@Builder
public class SpaceRequestDto {

    @NotEmptyAndNotBlank("googlePlacesIdentifier")
    private final String googlePlacesIdentifier;
}
