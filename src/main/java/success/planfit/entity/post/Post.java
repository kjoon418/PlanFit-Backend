package success.planfit.entity.post;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;
import static jakarta.persistence.GenerationType.IDENTITY;

import lombok.Getter;
import success.planfit.entity.course.Course;
import success.planfit.entity.user.User;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Post {

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
    private String content;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false)
    private Long likeCount;

    @Builder
    private Post(
            Course course,
            User user,
            String content,
            String title,
            LocalDateTime createdAt,
            Boolean isPublic
    ) {
        this.course = course;
        this.user = user;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.isPublic = isPublic;
        this.likeCount = 0L;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
