package success.planfit.entity.space;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
public class SpaceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String googlePlacesIdentifier;

    @Column(nullable = false)
    private String spaceName;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    private SpaceType spaceType;

    @Column(nullable = false)
    private String link;

    private Double latitude;

    private Double longitude;

    @Lob
    private byte[] spacePhoto;

    @Builder
    private SpaceDetail(
            String googlePlacesIdentifier,
            String spaceName,
            String location,
            SpaceType spaceType,
            String link,
            Double latitude,
            Double longitude,
            byte[] spacePhoto
    ) {
        this.googlePlacesIdentifier = googlePlacesIdentifier;
        this.spaceName = spaceName;
        this.location = location;
        this.spaceType = spaceType;
        this.link = link;
        this.latitude = latitude;
        this.longitude = longitude;
        this.spacePhoto = spacePhoto;
    }

}
