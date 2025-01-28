package success.planfit.domain;


import jakarta.persistence.*;
import lombok.*;
import success.planfit.domain.embeddable.SpaceInformation;
import success.planfit.domain.user.User;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
public class CachePlacedetail {

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
    private CachePlacedetail(User user, String googlePlacesIdentifier, SpaceInformation spaceInformation) {
        this.user = user;
        this.googlePlacesIdentifier = googlePlacesIdentifier;
        this.spaceInformation = spaceInformation;
    }

}
