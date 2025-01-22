package success.planfit.domain.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.*;
import success.planfit.domain.course.SpaceType;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SpaceInformation {

    @Column(nullable = false)
    private String spaceName;

    @Column(nullable = false)
    private String location;

    private SpaceType spaceTag;

    @Column(nullable = false)
    private String link;

    private Double latitude;

    private Double longitude;

    @Lob
    private byte[] spacePhoto;

}
