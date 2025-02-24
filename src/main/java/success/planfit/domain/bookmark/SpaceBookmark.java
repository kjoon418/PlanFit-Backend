package success.planfit.domain.bookmark;

import jakarta.persistence.*;
import lombok.*;
import success.planfit.domain.embeddable.SpaceInformation;
import success.planfit.domain.user.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name= "space_bookmark_uq_user_id_google_places_identifier", columnNames={"user_id", "google_places_identifier"}))
public class SpaceBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private String googlePlacesIdentifier;

    @Embedded
    private SpaceInformation spaceInformation;

    @Builder
    private SpaceBookmark(User user, String googlePlacesIdentifier, SpaceInformation spaceInformation) {
        this.user = user;
        this.googlePlacesIdentifier = googlePlacesIdentifier;
        this.spaceInformation = spaceInformation;
    }

}
