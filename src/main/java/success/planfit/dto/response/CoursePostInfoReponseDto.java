package success.planfit.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import success.planfit.domain.course.SpaceType;
import success.planfit.domain.embeddable.SpaceInformation;
import success.planfit.domain.post.CoursePost;
import success.planfit.domain.post.SpacePost;
import success.planfit.photo.PhotoProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Builder
public class CoursePostInfoReponseDto {
    // entity -> dto
    private Long id;
    private String title;
    private String location;
    private String titlePhoto;
    private List<Space> spaces;

    public static CoursePostInfoReponseDto from(CoursePost coursePost) {
        List<SpacePost> spacePosts = coursePost.getSpacePosts();
        List<Space> spaces = new ArrayList<>();

        // SpacePost로부터 Space 생성
        for (SpacePost spacePost : spacePosts) {
            SpaceInformation spaceInformation = spacePost.getSpaceInformation();
            spaces.add(Space.builder()
                    .spaceId(spacePost.getId())
                    .sequence(spacePost.getSequence())
                    .spaceName(spaceInformation.getSpaceName())
                    .location(spaceInformation.getLocation())
                    .spaceType(spaceInformation.getSpaceTag())
                    .link(spaceInformation.getLink())
                    .latitude(spaceInformation.getLatitude())
                    .longitude(spaceInformation.getLongitude())
                    .spacePhoto(PhotoProvider.encode(spaceInformation.getSpacePhoto()))
                    .build());
        }


        // 모은 Space를 sequence에 따라 정렬
        Collections.sort(spaces);

        return CoursePostInfoReponseDto.builder()
                .id(coursePost.getId())
                .title(coursePost.getTitle())
                .location(coursePost.getLocation()) // location
                .titlePhoto(PhotoProvider.encode(coursePost.getTitlePhoto()))
                .spaces(spaces)
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    static class Space implements Comparable<Space> {

        private Long spaceId;
        private Integer sequence;
        private String spaceName;
        private String location;
        private SpaceType spaceType;
        private String link;
        private Double latitude;
        private Double longitude;
        private String spacePhoto;

        @Override
        public int compareTo(Space s) {
            return this.sequence - s.sequence;
        }
    }

}
