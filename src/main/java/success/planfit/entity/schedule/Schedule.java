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
public class Schedule implements Comparable<Schedule> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Setter
    @OneToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private Course course;

    @Setter
    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(nullable = false)
    private LocalDate date;

    @Setter
    private String content;

    @Setter
    private String shareSerial;

    @Setter
    @Column(nullable = false)
    private LocalTime startTime;

    @Setter
    @Column(nullable = false)
    private Integer currentSequence;

    private String content;

    @Builder
    private Schedule(
            User user,
            String title,
            LocalDate date,
            String shareSerial,
            LocalTime startTime
    ) {
        this.user = user;
        this.title = title;
        this.date = date;
        this.shareSerial = shareSerial;
        this.startTime = startTime;
        this.currentSequence = 0;
    }

    @Override
    public int compareTo(Schedule other) {
        return this.getDate().compareTo(other.getDate());
    }

}
