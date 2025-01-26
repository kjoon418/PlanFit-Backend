package success.planfit.domain.course;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import success.planfit.domain.user.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
@Entity
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "calendar")
    private final List<Timetable> timetables = new ArrayList<>();

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate date;

    private String shareSerial;

    @Builder
    private Calendar(User user, String title, LocalDate date, String shareSerial) {
        this.user = user;
        this.title = title;
        this.date = date;
        this.shareSerial = shareSerial;
    }

    public void addTimetable(Timetable timetable) {
        this.timetables.add(timetable);
        timetable.setCalendar(this);
    }

    public void removeTimetable(Timetable timetable) {
        this.timetables.remove(timetable);
        timetable.setCalendar(null);
    }

}
