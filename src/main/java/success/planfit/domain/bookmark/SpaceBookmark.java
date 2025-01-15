package success.planfit.domain.bookmark;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import success.planfit.domain.user.User;

@Getter
@NoArgsConstructor
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

    @Builder
    private SpaceBookmark(User user, String googlePlacesIdentifier) {
        this.user = user;
        this.googlePlacesIdentifier = googlePlacesIdentifier;
    }

}
