package success.planfit.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import success.planfit.domain.bookmark.SpaceBookmark;

@Setter
@NoArgsConstructor
@Getter
public class PlaceDetailRequestDto {
    private Double latitude;
    private Double longitude;
    private Integer radius;
}
