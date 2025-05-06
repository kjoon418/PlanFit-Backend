package success.planfit.entity.rating;

import jakarta.persistence.*;
import lombok.*;
import success.planfit.entity.schedule.Schedule;
import success.planfit.entity.space.SpaceDetail;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "rating_uq_schedule_id_space_detail_id", columnNames = {"schedule_id", "space_detail_id"})
})
public class Rating {

    private static final int MINIMUM_VALUE = 1;
    private static final int MAXIMUM_VALUE = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Schedule schedule;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private SpaceDetail spaceDetail;

    @Column(nullable = false)
    private Integer value;

    @Builder
    private Rating(
            Schedule schedule,
            SpaceDetail spaceDetail,
            int value
    ) {
        validateValue(value);

        this.schedule = schedule;
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
