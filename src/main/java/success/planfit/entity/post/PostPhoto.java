package success.planfit.entity.post;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class PostPhoto {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = LAZY)
    private Post post;

    @Column(nullable = false)
    @Lob
    private byte[] photo;

    @Builder
    private PostPhoto(
            Post post,
            byte[] photo
    ) {
        this.post = post;
        this.photo = photo;
    }

}
