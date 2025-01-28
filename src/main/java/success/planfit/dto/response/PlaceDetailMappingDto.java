package success.planfit.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import success.planfit.domain.course.SpaceType;

@ToString
@Setter
@Getter
public class PlaceDetailMappingDto {

    @JsonProperty("id")
    private String googlePlacesIdentifier;

    // 내부 클래스로 구현
    @JsonProperty("displayName")
    private DisplayName spaceName;

    // 형식화된 위치 이름
    @JsonProperty("formattedAddress")
    private String location;

    // 타입 저장하는 방식
    private SpaceType spaceType;

    @JsonProperty("googleMapsUri")
    private String link;

    // 내부 클래스로 분리 - 위도 경도
    @JsonProperty("location")
    private Location locations;

    // 내부 클래스로 구현
    private GoogleMapLinks googleMapLinks;

    @Builder
    public PlaceDetailMappingDto(String googlePlacesIdentifier, DisplayName spaceName, String location, Location locations,SpaceType spaceType, String link, GoogleMapLinks googleMapLinks) {
        this.googlePlacesIdentifier = googlePlacesIdentifier;
        this.spaceName = spaceName;
        this.location = location;
        this.spaceType = spaceType;
        this.link = link;
        this.locations = locations;
        this.googleMapLinks = googleMapLinks;
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
