package success.planfit.domain.bookmark;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import success.planfit.domain.course.Timetable;
import success.planfit.domain.embeddable.SpaceInformation;

@Getter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "timetable_bookmark_uq_start_time", columnNames = {"course_bookmark_id", "start_time"}),
        @UniqueConstraint(name = "timetable_bookmark_uq_end_time", columnNames = {"course_bookmark_id", "end_time"})
})
public class TimetableBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CourseBookmark courseBookmark;

    @Setter
    @Column(nullable = false, unique = true)
    private Integer sequence;

    @Setter
    private String memo;

    @Setter
    @Embedded
    private SpaceInformation spaceInformation;

    @Builder
    private TimetableBookmark(CourseBookmark courseBookmark, Integer sequence, String memo, SpaceInformation spaceInformation) {
        this.courseBookmark = courseBookmark;
        this.sequence = sequence;
        this.memo = memo;
        this.spaceInformation = spaceInformation;
    }

    /**
     * Timetable을 통해 TimetableBookmark를 생성하는 정적 팩터리 메서드
     */
    public static TimetableBookmark from(Timetable timetable) {
        return TimetableBookmark.builder()
                .sequence(timetable.getSequence())
                .memo(timetable.getMemo())
                .spaceInformation(timetable.getSpaceInformation())
                .build();
    }
}
