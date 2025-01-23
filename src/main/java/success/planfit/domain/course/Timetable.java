package success.planfit.domain.course;


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

    private String memo;

    @Embedded
    private SpaceInformation spaceInformation;

    @Builder
    private Timetable(Calendar calendar,LocalTime startTime, LocalTime endTime, String memo, SpaceInformation spaceInformation) {
        this.calendar = calendar;
        this.startTime = startTime;
        this.endTime = endTime;
        this.memo = memo;
        this.spaceInformation = spaceInformation;
    }

}
