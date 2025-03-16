package success.planfit.space.dto.request;


import lombok.Builder;
import lombok.Getter;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.course.SpaceInformation;
import success.planfit.entity.space.SpaceType;
import success.planfit.global.photo.PhotoProvider;
import success.planfit.space.dto.PlaceDetailMappingDto;

@Getter
@Builder
public class CachePlaceDetailSaveRequestDto {

    private String googlePlacesIdentifier;
    private String spaceName;
    private String location;
    private SpaceType spaceTag;
    private String link;
    private Double latitude;
    private Double longitude;
    private String spacePhoto; // 우선 Base64로 인코딩된 사진 정보를 받아온다고 가정함

    // toEntity (CachePlaceDetail에 저장하는 용도!)
    public SpaceDetail toEntity() {
        return SpaceDetail.builder()
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

    // mappingDto -> saveDto
    public static CachePlaceDetailSaveRequestDto createSaveDtoFromMapper(PlaceDetailMappingDto mapper){
        return CachePlaceDetailSaveRequestDto.builder()
                .googlePlacesIdentifier(mapper.getGooglePlacesIdentifier())
                .spaceName(mapper.getSpaceName())
                .location(mapper.getLocation())
                .spaceTag(mapper.getSpaceTag())
                .link(mapper.getLink())
                .latitude(mapper.getLat())
                .longitude(mapper.getLng())
                .spacePhoto(mapper.getSpacePhoto())
                .build();


    }
}
