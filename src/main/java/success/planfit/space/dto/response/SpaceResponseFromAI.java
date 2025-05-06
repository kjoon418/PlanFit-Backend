package success.planfit.space.dto.response;


import lombok.Getter;
import success.planfit.entity.space.SpaceType;

import java.util.List;

@Getter
public class SpaceResponseFromAI {
    // 유사도
    private int similarityOrder;
    // 장소 정보
    private String googlePlacesIdentifier;
    private String location;
    private SpaceType spaceType;
    private String link;
    private Double latitude;
    private String name;
    private Double longitude;
    private List<String> spacePhotos;
}
