package success.planfit.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import success.planfit.entity.space.Space;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.space.SpacePhoto;
import success.planfit.entity.space.SpaceType;
import success.planfit.global.photo.PhotoProvider;

import java.util.List;

import static lombok.AccessLevel.*;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class SpaceResponseDto implements Comparable<SpaceResponseDto>{

    private final int sequence;
    private final String googlePlacesIdentifier;
    private final String name;
    private final String location;
    private final SpaceType spaceType;
    private final String link;
    private final Double latitude;
    private final Double longitude;
    private final long likeCount;
    private final List<String> spacePhotos;

    public static SpaceResponseDto createSpaceDto(Space space) {
        SpaceDetail spaceDetail = space.getSpaceDetail();
        List<String> spacePhotos = spaceDetail.getSpacePhotos().stream()
                .map(SpacePhoto::getValue)
                .map(PhotoProvider::encode)
                .toList();

        return SpaceResponseDto.builder()
                .sequence(space.getSequence())
                .googlePlacesIdentifier(spaceDetail.getGooglePlacesIdentifier())
                .name(spaceDetail.getSpaceName())
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
    public int compareTo(SpaceResponseDto requestDto) {
        if (this.sequence > requestDto.sequence)
            return 1;
        else if (this.sequence == requestDto.sequence)
            return 0;
        else
            return -1;
    }

}
