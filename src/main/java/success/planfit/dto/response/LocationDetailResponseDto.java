package success.planfit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import success.planfit.domain.user.UserDto;
import success.planfit.dto.request.PlaceDetailRequestDto;


@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class LocationDetailResponseDto {
    private UserDto userDto;
    private PlaceDetailRequestDto placeDetailRequestDto;


}
