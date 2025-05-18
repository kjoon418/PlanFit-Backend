package success.planfit.course.dto;

import lombok.*;
import success.planfit.global.validation.NotEmptyAndNotBlank;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class SpaceRequestDto {

    @NotEmptyAndNotBlank("googlePlacesIdentifier")
    private final String googlePlacesIdentifier;

}
