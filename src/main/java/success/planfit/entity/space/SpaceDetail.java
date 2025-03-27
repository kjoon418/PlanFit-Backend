package success.planfit.entity.space;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;

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

    @OneToMany(mappedBy = "spaceDetail", orphanRemoval = true, cascade = ALL)
    private List<SpacePhoto> spacePhotos = new ArrayList<>();

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

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private SpaceDetail(
            List<SpacePhoto> spacePhotos,
            String googlePlacesIdentifier,
            String spaceName,
            String location,
            SpaceType spaceType,
            String link,
            Double latitude,
            Double longitude
    ) {
        this.spacePhotos = spacePhotos;
        this.googlePlacesIdentifier = googlePlacesIdentifier;
        this.spaceName = spaceName;
        this.location = location;
        this.spaceType = spaceType;
        this.link = link;
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     *
     *  장소 사진
     */
    public void addSpacePhoto(SpacePhoto spacePhoto){
        spacePhotos.add(spacePhoto);
        spacePhoto.setSpaceDetail(this);
    }

}
