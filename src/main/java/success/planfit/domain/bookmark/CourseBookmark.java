package success.planfit.domain.bookmark;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import success.planfit.domain.user.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class CourseBookmark {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(nullable=false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "courseBookmark")
    private final List<TimetableBookmark> timetableBookmarks = new ArrayList<>();

    @Setter
    private String title;

    @Lob
    @Setter
    private byte[] titlePhoto;

    @Builder
    private CourseBookmark(User user, String title, byte[] titlePhoto) {
        this.user = user;
        this.title = title;
        this.titlePhoto = titlePhoto;
    }

    /**
     * CourseBookmark - TimetableBookmark 연관관계 편의 메서드(생성)
     */
    public void addTimetableBookmark(TimetableBookmark timetableBookmark) {
        this.timetableBookmarks.add(timetableBookmark);
        timetableBookmark.setCourseBookmark(this);
    }

    /**
     * CourseBookmark - TimetableBookmark 연관관계 편의 메서드(삭제)
     */
    public void removeTimetableBookmark(TimetableBookmark timetableBookmark) {
        this.timetableBookmarks.remove(timetableBookmark);
        timetableBookmark.setCourseBookmark(null);
    }
}
