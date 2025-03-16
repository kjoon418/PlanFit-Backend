package success.planfit.domain.bookmark;

import jakarta.persistence.*;
import lombok.Getter;
import success.planfit.domain.post.CoursePost;
import success.planfit.domain.user.User;

@Entity
@Getter
public class CoursePostBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CoursePost coursePost;




}
