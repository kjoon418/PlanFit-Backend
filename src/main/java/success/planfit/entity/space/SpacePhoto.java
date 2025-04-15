package success.planfit.entity.space;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import success.planfit.global.photo.PhotoProvider;

import java.util.List;

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

    @Setter
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

    public static List<SpacePhoto> createSpacePhoto(List<String> spacePhotos){
        return spacePhotos.stream()
                .map(PhotoProvider::decode)
                .map(spacePhotoForFilter ->
                {return SpacePhoto.builder()
                        .value(spacePhotoForFilter)
                        .build();})
                .toList();
    }
}
