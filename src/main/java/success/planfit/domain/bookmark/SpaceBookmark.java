package success.planfit.domain.bookmark;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import success.planfit.domain.course.SpaceType;
import success.planfit.domain.embeddable.SpaceInformation;
import success.planfit.domain.user.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name= "space_bookmark_uq_space", columnNames={"user_id", "google_places_identifier"}))
public class SpaceBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private String googlePlacesIdentifier;

    @Column(nullable = false)
    private String spaceName;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SpaceType spaceType;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private byte[] spacePhoto;


    @Embedded
    private SpaceInformation spaceInformation;

    @Builder
    private SpaceBookmark(User user, String googlePlacesIdentifier, SpaceInformation spaceInformation) {
        this.user = user;
        this.googlePlacesIdentifier = googlePlacesIdentifier;
        this.spaceInformation = spaceInformation;
    }

}
