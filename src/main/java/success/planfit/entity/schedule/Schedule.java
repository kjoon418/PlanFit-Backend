package success.planfit.entity.schedule;

import jakarta.persistence.*;
import lombok.*;
import success.planfit.entity.course.Course;
import success.planfit.entity.rating.Rating;
import success.planfit.entity.user.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Schedule implements Comparable<Schedule> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true, mappedBy = "schedule")
    private final List<Rating> ratings = new ArrayList<>();

    @Setter
    @OneToOne(fetch = LAZY, cascade = ALL, orphanRemoval = true)
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

    @Column(nullable = false)
    private Boolean ratingRequested;

    @Builder
    private Schedule(
            String title,
            LocalDate date,
            String content,
            LocalTime startTime
    ) {
        this.title = title;
        this.date = date;
        this.content = content;
        this.startTime = startTime;
        this.currentSequence = 0;
        this.ratingRequested = false;
    }

    public void recordRatingRequest() {
        this.ratingRequested = true;
    }

    @Override
    public int compareTo(Schedule other) {
        return this.getDate().compareTo(other.getDate());
    }

    /**
     * Schedule - Rating 연관관계 편의 메서드(생성)
     */
    public void addRating(Rating rating) {
        ratings.add(rating);
        rating.setSchedule(this);
    }

    /**
     * Schedule - Rating 연관관계 편의 메서드(전체 삭제)
     */
    public void clearRatings() {
        ratings.clear();
    }

}
