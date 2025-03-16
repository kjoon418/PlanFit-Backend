package success.planfit.space.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Getter
public class PlaceDetailRequestDto {
    private Double latitude;
    private Double longitude;
}
