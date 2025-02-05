package success.planfit.domain;


import jakarta.persistence.*;
import lombok.*;
import success.planfit.domain.embeddable.SpaceInformation;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
public class CachePlaceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String googlePlacesIdentifier;

    @Embedded
    private SpaceInformation spaceInformation;

    @Builder
    private CachePlaceDetail(String googlePlacesIdentifier, SpaceInformation spaceInformation) {
        this.googlePlacesIdentifier = googlePlacesIdentifier;
        this.spaceInformation = spaceInformation;
    }

}
