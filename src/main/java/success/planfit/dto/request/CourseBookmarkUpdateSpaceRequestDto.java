package success.planfit.dto.request;

import lombok.Getter;
import success.planfit.domain.course.SpaceType;
import success.planfit.domain.embeddable.SpaceInformation;
import success.planfit.photo.PhotoProvider;

@Getter
public class CourseBookmarkUpdateSpaceRequestDto {

    private String memo;
    private String spaceName;
    private String location;
    private SpaceType spaceType;
    private String link;
    private Double latitude;
    private Double longitude;
    private String spacePhoto;

    public SpaceInformation getSpaceInformation() {
        return SpaceInformation.builder()
                .spaceName(spaceName)
                .location(location)
                .spaceTag(spaceType)
                .link(link)
                .latitude(latitude)
                .longitude(longitude)
                .spacePhoto(PhotoProvider.decode(spacePhoto))
                .build();
    }
}
