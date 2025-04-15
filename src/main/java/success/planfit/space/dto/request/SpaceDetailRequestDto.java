package success.planfit.space.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class SpaceDetailRequestDto {
    private Double latitude;
    private Double longitude;
    private String spaceType;
}
