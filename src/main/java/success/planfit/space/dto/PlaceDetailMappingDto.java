package success.planfit.space.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import success.planfit.entity.space.SpaceType;

@Getter
public class PlaceDetailMappingDto {

    @JsonProperty("id")
    private String googlePlacesIdentifier;

    // 내부 클래스로 구현
    @JsonProperty("displayName")
    private DisplayName displayName;
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
    private Location locations;

    private Double lat;
    private Double lng;

    // 내부 클래스로 구현
    @JsonProperty("googleMapLinks")
    private GoogleMapLinks googleMapLinks;
    private String  spacePhoto;

    // json과 맵핑하기 위한 생성자
    @Builder
    public PlaceDetailMappingDto(String googlePlacesIdentifier, DisplayName displayName, String location, Location locations, SpaceType spaceTag, String link, GoogleMapLinks googleMapLinks) {
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
