package success.planfit.like.dto.response;

import lombok.Builder;
import success.planfit.entity.like.SpaceBookmark;
import success.planfit.entity.space.SpaceType;
import success.planfit.global.photo.PhotoProvider;

@Builder
public record SpaceBookmarkInfoResponseDto(
        String googlePlacesIdentifier,
        String spaceName,
        String location,
        SpaceType spaceType,
        String link,
        Double latitude,
        Double longitude,
        String spacePhoto // Base64를 통해 인코딩된 바이트 배열
) {

    public static SpaceBookmarkInfoResponseDto of(SpaceBookmark spaceBookmark) {
        return SpaceBookmarkInfoResponseDto.builder()
                .googlePlacesIdentifier(spaceBookmark.getGooglePlacesIdentifier())
                .spaceName(spaceBookmark.getSpaceInformation().getSpaceName())
                .location(spaceBookmark.getSpaceInformation().getLocation())
                .spaceType(spaceBookmark.getSpaceInformation().getSpaceTag())
                .link(spaceBookmark.getSpaceInformation().getLink())
                .latitude(spaceBookmark.getSpaceInformation().getLatitude())
                .longitude(spaceBookmark.getSpaceInformation().getLongitude())
                .spacePhoto(PhotoProvider.encode(spaceBookmark.getSpaceInformation().getSpacePhoto()))
                .build();
    }
}
