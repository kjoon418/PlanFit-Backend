package success.planfit.entity.space;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @ManyToAny(fetch = LAZY)
    @JoinColumn(nullable = false)
    private Course course;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private SpaceDetail spaceDetail;

    @Builder
    private Space(
        Course course,
        SpaceDetail spaceDetail
    ) {
        this.course = course;
        this.spaceDetail = spaceDetail;
    }

}
