package success.planfit.domain.bookmark;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import success.planfit.domain.embeddable.SpaceInformation;

import java.time.LocalTime;

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

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CourseBookmark courseBookmark;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    private String memo;

    @Embedded
    private SpaceInformation spaceInformation;

    @Builder
    private TimetableBookmark(CourseBookmark courseBookmark, LocalTime startTime, LocalTime endTime, String memo, SpaceInformation spaceInformation) {
        this.courseBookmark = courseBookmark;
        this.startTime = startTime;
        this.endTime = endTime;
        this.memo = memo;
        this.spaceInformation = spaceInformation;
    }
}
