package success.planfit.entity.space;

import jakarta.persistence.*;
import lombok.*;
import success.planfit.entity.rating.Rating;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class SpaceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "spaceDetail", orphanRemoval = true, cascade = ALL)
    private List<SpacePhoto> spacePhotos = new ArrayList<>();

    @OneToMany(mappedBy = "spaceDetail", orphanRemoval = true, cascade = ALL)
    private List<Rating> ratings = new ArrayList<>();

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
    private long likeCount;

    @Builder
    private SpaceDetail(
            String googlePlacesIdentifier,
            String spaceName,
            String location,
            SpaceType spaceType,
            String link,
            Double latitude,
            Double longitude,
            long likeCount
    ) {
        this.googlePlacesIdentifier = googlePlacesIdentifier;
        this.spaceName = spaceName;
        this.location = location;
        this.spaceType = spaceType;
        this.link = link;
        this.latitude = latitude;
        this.longitude = longitude;
        this.likeCount = 0L;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /**
     * SpaceDetail - Rating 연관관계 편의 메서드(생성)
     */
    public void addRating(Rating rating) {
        this.ratings.add(rating);
        rating.setSpaceDetail(this);
    }

    public void addSpacePhotos(List<SpacePhoto> spacePhotos){
        for (SpacePhoto spacePhoto : spacePhotos) {
            spacePhoto.setSpaceDetail(this);
        }

        this.spacePhotos.addAll(spacePhotos);
    }

}
