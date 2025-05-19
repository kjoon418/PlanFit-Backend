package success.planfit.space.dto.request;

import lombok.Getter;

@Getter
public class SpaceDetailRequestDto {
    private Double latitude;
    private Double longitude;
    private String spaceType;
    private double radius;
}
