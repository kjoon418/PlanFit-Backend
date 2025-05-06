package success.planfit.fixture;

import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.space.SpaceType;

import java.util.Arrays;
import java.util.List;

public enum SpaceDetailFixture {

    RESTAURANT(
            "RESTAURANT_ID",
            "RESTAURANT_NAME",
            "RESTAURANT_LOCATION",
            SpaceType.TYPE,
            "RESTAURANT_LINK",
            1.23456,
            2.34567
    ),
    CAFE(
            "CAFE_ID",
            "CAFE_NAME",
            "CAFE_LOCATION",
            SpaceType.TYPE,
            "CAFE_LINK",
            12.3456,
            23.4567
    ),
    PLAYGROUND(
            "PLAYGROUND_ID",
            "PLAYGROUND_NAME",
            "PLAYGROUND_LOCATION",
            SpaceType.TYPE,
            "PLAYGROUND_LINK",
            1.23456,
            2.34567
    );

    private final String googlePlacesIdentifier;
    private final String spaceName;
    private final String location;
    private final SpaceType spaceType;
    private final String link;
    private final Double latitude;
    private final Double longitude;

    SpaceDetailFixture(
            String googlePlacesIdentifier,
            String spaceName,
            String location,
            SpaceType spaceType,
            String link,
            Double latitude,
            Double longitude
    ) {
        this.googlePlacesIdentifier = googlePlacesIdentifier;
        this.spaceName = spaceName;
        this.location = location;
        this.spaceType = spaceType;
        this.link = link;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static List<SpaceDetail> createInstances() {
        return Arrays.stream(values())
                .map(SpaceDetailFixture::createInstance)
                .toList();
    }

    public static List<SpaceDetail> createInstancesWith(String suffixValue) {
        return Arrays.stream(values())
                .map(spaceDetailFixture -> spaceDetailFixture.createInstanceWith(suffixValue))
                .toList();
    }

    public SpaceDetail createInstance() {
        return SpaceDetail.builder()
                .googlePlacesIdentifier(googlePlacesIdentifier)
                .spaceName(spaceName)
                .location(location)
                .spaceType(spaceType)
                .link(link)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    public SpaceDetail createInstanceWith(String suffixValue) {
        return SpaceDetail.builder()
                .googlePlacesIdentifier(googlePlacesIdentifier + suffixValue)
                .spaceName(spaceName + suffixValue)
                .location(location + suffixValue)
                .spaceType(spaceType)
                .link(link + suffixValue)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

}
