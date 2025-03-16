package success.planfit.entity.space;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ManyToAny;
import success.planfit.entity.course.Course;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Space {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Setter
    @ManyToAny(fetch = LAZY)
    @JoinColumn(nullable = false)
    private Course course;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private SpaceDetail spaceDetail;

    private Integer sequence;

    private Long likeCount;

    @Builder
    private Space(
        SpaceDetail spaceDetail,
        Integer sequence
    ) {
        this.spaceDetail = spaceDetail;
        this.sequence = sequence;
        this.likeCount = 0L;
    }

    public static Space copyOf(Space original) {
        return Space.builder()
                .spaceDetail(original.spaceDetail)
                .sequence(original.sequence)
                .build();
    }

}
