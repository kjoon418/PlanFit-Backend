package success.planfit.dto.request;
import lombok.Getter;
import success.planfit.domain.course.SpaceType;

@Getter
public class PlaceRelevanceDetail {
    private String placeId;
    private SpaceType spaceType;
    private Double relevance;
}
