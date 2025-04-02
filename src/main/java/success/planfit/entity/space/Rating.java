package success.planfit.entity.space;

import jakarta.persistence.*;
import lombok.*;
import success.planfit.entity.user.User;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Rating {

    private static final int MINIMUM_VALUE = 1;
    private static final int MAXIMUM_VALUE = 5;

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
            int value
    ) {
        validateValue(value);

        this.user = user;
        this.spaceDetail = spaceDetail;
        this.value = value;
    }

    private void validateValue(int value) {
        if (isOutOfRange(value)) {
            throw new IllegalArgumentException("별점은 " + MINIMUM_VALUE + " ~ " + MAXIMUM_VALUE + "사이의 값이어야 합니다. 전달된 값: " + value);
        }
    }

    private boolean isOutOfRange(int value) {
        return MAXIMUM_VALUE < value || value < MINIMUM_VALUE;
    }

}
