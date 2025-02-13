package success.planfit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import success.planfit.domain.bookmark.SpaceBookmark;
import success.planfit.domain.course.SpaceType;
import success.planfit.domain.embeddable.SpaceInformation;
import success.planfit.photo.PhotoProvider;

@Builder
@AllArgsConstructor
@Getter
public class SpaceBookmarkRegistrationRequestDto {

    private String googlePlacesIdentifier;
    private String spaceName;
    private String location;
    private SpaceType spaceType;
    private String link;
    private Double latitude;
    private Double longitude;
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
