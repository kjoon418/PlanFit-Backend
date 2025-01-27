package success.planfit.domain.course;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import success.planfit.domain.embeddable.SpaceInformation;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Entity

public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Calendar calendar;

    @Setter
    private Integer sequence;

    @Setter
    private String memo;

    @Setter
    @Embedded
    private SpaceInformation spaceInformation;

    @Builder
    private Timetable(Calendar calendar, String memo, SpaceInformation spaceInformation, Integer sequence) {
        this.calendar = calendar;
        this.sequence = sequence;
        this.memo = memo;
        this.spaceInformation = spaceInformation;
    }

}
