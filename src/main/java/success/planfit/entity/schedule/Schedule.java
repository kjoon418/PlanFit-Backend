package success.planfit.entity.schedule;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import success.planfit.entity.course.Course;
import success.planfit.entity.user.User;

import java.time.LocalDate;
import java.time.LocalTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private Course course;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate date;

    @Setter
    private String shareSerial;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private Integer sequence;

    @Builder
    private Schedule(
            Course course,
            User user,
            String title,
            LocalDate date,
            String shareSerial,
            LocalTime startTime
    ) {
        this.course = course;
        this.user = user;
        this.title = title;
        this.date = date;
        this.shareSerial = shareSerial;
        this.startTime = startTime;
    }

}
