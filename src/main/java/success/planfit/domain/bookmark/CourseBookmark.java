package success.planfit.domain.bookmark;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import success.planfit.domain.user.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class CourseBookmark {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable=false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "courseBookmark")
    private final List<TimetableBookmark> timetableBookmarks = new ArrayList<>();

    private String title;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    private String titlePhoto;

    @Builder
    private CourseBookmark(User user, String title, LocalDate date, String titlePhoto) {
        this.user = user;
        this.title = title;
        this.date = date;
        this.titlePhoto = titlePhoto;
    }

}
