package success.planfit.domain.post;

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
public class CoursePost {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "course_post_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "spacePosts")
    private List<SpacePost> spacePosts = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "course_post_bookmark"
            , joinColumns = @JoinColumn(name = "course_post_id")
            , inverseJoinColumns = @JoinColumn(name = "id"))
    private List<User> users = new ArrayList<>();

    @Column(nullable = false)
    private String title;

    private String location;

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

    /**
     * CoursePost - CoursePostBookmark 연관관계 편의 메서드(삭제)
     */
    public void addUsers(User user) {
        users.add(user);
        user.getCoursePostBookmarks().add(this);
    }

    /**
     *  CoursePost - CoursePostBookmark 연관관계 편의 메서드(삭제)
     */
    public void removeUsers(User user) {
        users.remove(user);
        user.getCoursePostBookmarks().remove(this);
    }

}
