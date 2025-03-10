package success.planfit.dto.request;

import lombok.Getter;
import success.planfit.domain.bookmark.SpaceBookmark;
import success.planfit.domain.course.SpaceType;
import success.planfit.domain.embeddable.SpaceInformation;
import success.planfit.photo.PhotoProvider;
import success.planfit.validation.NotEmptyAndNotBlank;

@Getter
public class SpaceBookmarkRegistrationRequestDto {

    @NotEmptyAndNotBlank(value = "googlePlacesIdentifier")
    private String googlePlacesIdentifier;

    @NotEmptyAndNotBlank(value = "spaceName")
    private String spaceName;

    @NotEmptyAndNotBlank(value = "location")
    private String location;
    private SpaceType spaceTag;

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
                        .spaceTag(spaceTag)
                        .link(link)
                        .latitude(latitude)
                        .longitude(longitude)
                        .spacePhoto(PhotoProvider.decode(spacePhoto))
                        .build())
                .build();
    }

}
