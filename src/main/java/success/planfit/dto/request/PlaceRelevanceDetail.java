package success.planfit.dto.request;
import lombok.Getter;
import success.planfit.domain.course.SpaceType;

@Getter
public class PlaceRelevanceDetail {
    private Long placeId;
    private SpaceType spaceType;
    private Double relevance;
}
