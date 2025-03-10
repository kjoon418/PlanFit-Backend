package success.planfit.space.dto.request;
import lombok.Getter;
import success.planfit.entity.space.SpaceType;

@Getter
public class PlaceRelevanceDetail {
    private String placeId;
    private SpaceType spaceType;
    private Double relevance;
}
