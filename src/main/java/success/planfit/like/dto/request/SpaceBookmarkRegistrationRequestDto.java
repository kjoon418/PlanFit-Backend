package success.planfit.like.dto.request;

import lombok.Getter;
import success.planfit.entity.like.SpaceBookmark;
import success.planfit.entity.course.SpaceInformation;
import success.planfit.entity.space.SpaceType;
import success.planfit.global.photo.PhotoProvider;
import success.planfit.global.validation.NotEmptyAndNotBlank;

@Getter
public class SpaceBookmarkRegistrationRequestDto {

    @NotEmptyAndNotBlank(value = "googlePlacesIdentifier")
    private String googlePlacesIdentifier;

    @NotEmptyAndNotBlank(value = "spaceName")
    private String spaceName;

    @NotEmptyAndNotBlank(value = "location")
    private String location;

    @NotEmptyAndNotBlank(value = "spaceType")
    private SpaceType spaceType;

    @NotEmptyAndNotBlank(value = "link")
    private String link;

    @NotEmptyAndNotBlank(value = "latitude")
    private Double latitude;

    @NotEmptyAndNotBlank(value = "longitude")
    private Double longitude;

    @NotEmptyAndNotBlank(value = "spacePhoto")
    private String spacePhoto; // 우선 Base64로 인코딩된 사진 정보를 받아온다고 가정함

    public SpaceBookmark toEntity() {
        return SpaceBookmark.builder()
                .googlePlacesIdentifier(googlePlacesIdentifier)
                .spaceInformation(SpaceInformation.builder()
                        .spaceName(spaceName)
                        .location(location)
                        .spaceTag(spaceType)
                        .link(link)
                        .latitude(latitude)
                        .longitude(longitude)
                        .spacePhoto(PhotoProvider.decode(spacePhoto))
                        .build())
                .build();
    }

}
