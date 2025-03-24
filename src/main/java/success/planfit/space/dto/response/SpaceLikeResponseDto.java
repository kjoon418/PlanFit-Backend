package success.planfit.space.dto.response;

import lombok.Getter;
import success.planfit.entity.space.SpaceDetail;

@Getter
public class SpaceLikeResponseDto {
    private final String googlePlacesIdentifier;
    private final String spaceName;
    private final String location;
    private final String link;

    public SpaceLikeResponseDto(SpaceDetail spaceDetail) {
        this.googlePlacesIdentifier = spaceDetail.getGooglePlacesIdentifier();
        this.spaceName = spaceDetail.getSpaceName();
        this.location = spaceDetail.getLocation();
        this.link = spaceDetail.getLink();
    }

}
