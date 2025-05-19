package success.planfit.course.dto;

import lombok.Builder;
import lombok.Getter;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.space.SpacePhoto;
import success.planfit.entity.space.SpaceType;
import success.planfit.global.photo.PhotoProvider;

import java.util.List;

@Getter
@Builder
public class SpaceDetailInfoDto implements Comparable<SpaceDetailInfoDto> {
    private final String googlePlacesIdentifier;
    private final String spaceName;
    private final String location;
    private final SpaceType spaceType;
    private final String link;
    private final Double latitude;
    private final Double longitude;
    private final long likeCount;
    private final List<String> spacePhotos;
    private int similarityOrder;


    public static SpaceDetailInfoDto of(SpaceDetail spaceDetail) {
        List<String> spacePhotos = spaceDetail.getSpacePhotos().stream()
                .map(SpacePhoto::getValue)
                .map(PhotoProvider::encode)
                .toList();

        return SpaceDetailInfoDto.builder()
                .googlePlacesIdentifier(spaceDetail.getGooglePlacesIdentifier())
                .spaceName(spaceDetail.getSpaceName())
                .location(spaceDetail.getLocation())
                .spaceType(spaceDetail.getSpaceType())
                .link(spaceDetail.getLink())
                .latitude(spaceDetail.getLatitude())
                .longitude(spaceDetail.getLongitude())
                .likeCount(spaceDetail.getLikeCount())
                .spacePhotos(spacePhotos)
                .build();
    }

    @Override
    public int compareTo(SpaceDetailInfoDto o) {
        return this.similarityOrder - o.similarityOrder;
    }
}
