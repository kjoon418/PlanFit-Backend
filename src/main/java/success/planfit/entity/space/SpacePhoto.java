package success.planfit.entity.space;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class SpacePhoto {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    private SpaceDetail spaceDetail;

    @Lob
    @Column(nullable = false)
    private byte[] value;

    @Builder
    private SpacePhoto(
            SpaceDetail spaceDetail,
            byte[] value
    ) {
        this.spaceDetail = spaceDetail;
        this.value = value;
    }

}
