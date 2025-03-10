package success.planfit.space.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import success.planfit.space.dto.request.PlaceDetailRequestDto;
import success.planfit.user.dto.UserUpdateDto;


@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class LocationDetailResponseDto {
    // AI에게 전달하는 정보 - 유저 정보, 위치 정보
    private UserUpdateDto userUpdateDto;
    private PlaceDetailRequestDto placeDetailRequestDto;


}
