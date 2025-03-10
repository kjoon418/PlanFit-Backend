package success.planfit.dto.request;

import lombok.Getter;
import success.planfit.entity.space.SpaceType;
import success.planfit.entity.course.SpaceInformation;
import success.planfit.global.photo.PhotoProvider;

@Getter
public class TimetableUpdateRequestDto {

    private String memo;
    private String spaceName;
    private String location;
    private String link;
    private Double latitude;
    private Double longitude;
    private String spacePhoto;
    private SpaceType spaceType;

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
