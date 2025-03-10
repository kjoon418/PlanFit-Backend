package success.planfit.domain.post;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import success.planfit.domain.BaseEntity;
import success.planfit.domain.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class CoursePost extends BaseEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "course_post_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "coursePost")
    private List<SpacePost> spacePosts = new ArrayList<>();

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private byte[] titlePhoto;


    @Builder
    private CoursePost(User user, String title, String location, byte[] titlePhoto) {
        this.user = user;
        this.title = title;
        this.location = location;
        this.titlePhoto = titlePhoto;
    }


    /**
     * CoursePost - SpacePost 연관관계 편의 메서드(삭제)
     */
    public void addSpacePost(SpacePost spacePost) {
        spacePosts.add(spacePost);
        spacePost.setCoursePost(this);
    }

    /**
     *  CoursePost - SpacePost 연관관계 편의 메서드(삭제)
     */
    public void removeSpacePost(SpacePost spacePost) {
        spacePosts.remove(spacePost);
        spacePost.setCoursePost(null);
    }

//    /**
//     * CoursePost - CoursePostBookmark 연관관계 편의 메서드(삭제)
//     */
//    public void addUsers(User user) {
//        users.add(user);
//        user.getCoursePostBookmarks().add(this);
//    }
//
//    /**
//     *  CoursePost - CoursePostBookmark 연관관계 편의 메서드(삭제)
//     */
//    public void removeUsers(User user) {
//        users.remove(user);
//        user.getCoursePostBookmarks().remove(this);
//    }

}
