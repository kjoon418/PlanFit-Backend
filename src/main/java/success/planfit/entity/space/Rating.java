package success.planfit.entity.space;

import jakarta.persistence.*;
import lombok.*;
import success.planfit.entity.user.User;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private SpaceDetail spaceDetail;

    @Setter
    @Column(nullable = false)
    private Integer value;

    @Builder
    private Rating(
            User user,
            SpaceDetail spaceDetail,
            Integer value
    ) {
        this.user = user;
        this.spaceDetail = spaceDetail;
        this.value = value;
    }

}
