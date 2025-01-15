package success.planfit.domain.course;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "timetable_uq_start_time", columnNames = {"calendar_id", "start_time"}),
        @UniqueConstraint(name = "timetable_uq_end_time", columnNames = {"calendar_id", "end_time"})
})
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Calendar calendar;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private String spaceName;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    private SpaceType spaceTag;

    private String memo;

    @Column(nullable = false)
    private String link;

    @Builder
    private Timetable(Calendar calendar,LocalTime startTime, LocalTime endTime, String spaceName, String location, SpaceType spaceTag, String memo, String link) {
        this.calendar = calendar;
        this.startTime = startTime;
        this.endTime = endTime;
        this.spaceName = spaceName;
        this.location = location;
        this.spaceTag = spaceTag;
        this.memo = memo;
        this.link = link;
    }



}
