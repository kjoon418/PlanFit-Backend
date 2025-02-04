package success.planfit.domain.course;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import success.planfit.domain.embeddable.SpaceInformation;

@Getter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "timetable_uq_calendar_id_sequence", columnNames = {"calendar_id", "sequence"})
})
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Calendar calendar;

    @Column(nullable = false)
    @Setter
    private Integer sequence;

    @Setter
    private String memo;

    @Setter
    @Embedded
    private SpaceInformation spaceInformation;

    @Builder
    private Timetable(Calendar calendar, Integer sequence, String memo, SpaceInformation spaceInformation) {
        this.calendar = calendar;
        this.sequence = sequence;
        this.memo = memo;
        this.spaceInformation = spaceInformation;
    }

}
