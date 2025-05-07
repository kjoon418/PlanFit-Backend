package success.planfit.space.dto.request;

import lombok.Builder;
import lombok.Getter;
import success.planfit.course.dto.SpaceResponseDto;
import success.planfit.entity.user.User;
import success.planfit.post.dto.response.PostInfoDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class SpaceInfoForAIDto {
    private List<String> userKeywords;
    private String location;
    private double radius;
    private String query;

    public static SpaceInfoForAIDto of(SpaceDetailRequestDto requestDto){
        return SpaceInfoForAIDto.builder()
//                .userKeywords()
                .location(String.valueOf(requestDto.getLatitude()+ requestDto.getLongitude()))
                .radius(requestDto.getRadius())
                .query(requestDto.getSpaceType())
                .build();
    }
}
