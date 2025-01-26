package success.planfit.domain.embeddable;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;
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

    @Enumerated(EnumType.STRING)
    private SpaceType spaceTag;

    @Column(nullable = false)
    private String link;

    private Double latitude;

    private Double longitude;

    @Lob
    private byte[] spacePhoto;

    public SpaceInformation copyNotNulls(SpaceInformation newValue) {
        return SpaceInformation.builder().spaceName(isNull(newValue.spaceName) ? this.spaceName : newValue.spaceName)
                .location(isNull(newValue.location) ? this.location : newValue.location)
                .spaceTag(isNull(newValue.spaceTag) ? this.spaceTag : newValue.spaceTag)
                .link(isNull(newValue.link) ? this.link : newValue.link).
                latitude(isNull(newValue.latitude) ? this.latitude : newValue.latitude)
                .longitude(isNull(newValue.longitude) ? this.longitude : newValue.longitude)
                .spacePhoto(isNull(newValue.spacePhoto) ? this.spacePhoto : newValue.spacePhoto)
                .build();}
    private <T> boolean isNull(T value) {
        if (value == null) {
            return true;}

        // 문자열도 null도 아니면 true, 문자열이라면 StringUtils를 통과하지 못해야 true
        return !(value instanceof String) || !StringUtils.hasText((String) value);
    }

}
