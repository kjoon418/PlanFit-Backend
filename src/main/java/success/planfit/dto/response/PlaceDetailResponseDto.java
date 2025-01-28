package success.planfit.dto.response;

import lombok.*;
import success.planfit.domain.course.SpaceType;

@ToString
@Getter
@Builder
public class PlaceDetailResponseDto {

    private String googlePlacesIdentifier;
    private String spaceName;
    private String location;
    private SpaceType spaceType;
    private String link;
    private Double lat;
    private Double lng;
    private String googleMapLinks;

    // Mapping dto class에서 전달용 일반 dto로 만드는 과정 (생성자)
    public PlaceDetailResponseDto create(PlaceDetailMappingDto placeDetailMappingDto){
            return PlaceDetailResponseDto.builder()
                    .googlePlacesIdentifier(placeDetailMappingDto.getGooglePlacesIdentifier())
                    .spaceName(placeDetailMappingDto.getSpaceName().getDisplayName())
                    .location(placeDetailMappingDto.getLocation())
                    .spaceType(placeDetailMappingDto.getSpaceType())
                    .link(placeDetailMappingDto.getLink())
                    .lat(placeDetailMappingDto.getLocations().getLatitude())
                    .lng(placeDetailMappingDto.getLocations().getLongitude())
                    .googleMapLinks(placeDetailMappingDto.getGoogleMapLinks().getPhotosUri())
                    .build();
    }


}
