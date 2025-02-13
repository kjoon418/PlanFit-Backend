package success.planfit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import success.planfit.domain.CachePlaceDetail;
import success.planfit.domain.course.SpaceType;
import success.planfit.dto.response.PlaceDetailResponseDto;
import success.planfit.photo.PhotoProvider;

@Getter
public class PlaceDetailMappingDto {

    @JsonProperty("id")
    private String googlePlacesIdentifier;

    // 내부 클래스로 구현
    @JsonProperty("displayName")
    private PlaceDetailMappingDto.DisplayName displayName;
    private String spaceName;

    // 형식화된 위치 이름
    @JsonProperty("formattedAddress")
    private String location;

    // 타입 저장하는 방식
    private SpaceType spaceTag;

    @JsonProperty("googleMapsUri")
    private String link;

    // 내부 클래스로 분리 - 위도 경도
    @JsonProperty("location")
    private PlaceDetailMappingDto.Location locations;

    private Double lat;
    private Double lng;

    // 내부 클래스로 구현
    @JsonProperty("googleMapLinks")
    private PlaceDetailMappingDto.GoogleMapLinks googleMapLinks;
    private String  spacePhoto;

    // json과 맵핑하기 위한 생성자
    @Builder
    public PlaceDetailMappingDto(String googlePlacesIdentifier, PlaceDetailMappingDto.DisplayName displayName, String location, PlaceDetailMappingDto.Location locations, SpaceType spaceTag, String link, PlaceDetailMappingDto.GoogleMapLinks googleMapLinks) {
        this.googlePlacesIdentifier = googlePlacesIdentifier;
        this.spaceName = displayName.getDisplayName();
        this.location = location;
        this.spaceTag = spaceTag;
        this.link = link;
        this.lat = locations.getLatitude();
        this.lng = locations.getLongitude();
        this.spacePhoto = googleMapLinks.getPhotosUri();
    }

    @Getter
    @NoArgsConstructor
    public static class DisplayName {
        @JsonProperty("text")
        private String displayName;
    }

    @Getter
    @NoArgsConstructor
    public static class Location {
        private Double latitude;
        private Double longitude;
    }

    @Getter
    @NoArgsConstructor
    public static class GoogleMapLinks{
        private String photosUri;
    }



}
