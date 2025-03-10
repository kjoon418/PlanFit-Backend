package success.planfit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import success.planfit.domain.course.SpaceType;

@Getter
@Builder
@AllArgsConstructor
public class SpacePostInfoReponseDto{

    private Long spaceId;
    private Integer sequence;
    private String spaceName;
    private String location;
    private SpaceType spaceType;
    private String link;
    private Double latitude;
    private Double longitude;
    private String spacePhoto;
//
//    @Override
//    public int compareTo(CoursePostInfoReponseDto.Space s) {
//        return this.sequence - s.sequence;
//    }
}
