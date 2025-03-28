package success.planfit.entity.user;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import success.planfit.entity.comment.Comment;
import success.planfit.entity.post.Post;
import success.planfit.entity.schedule.Schedule;
import success.planfit.entity.like.*;
import success.planfit.global.jwt.RefreshToken;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;


@Getter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = ALL, orphanRemoval = true)
    @JoinColumn
    private RefreshToken refreshToken;

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "user")
    private final List<Post> posts = new ArrayList<>();

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "user")
    private final List<Schedule> schedules = new ArrayList<>();

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "user")
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "user")
    private final List<SpaceLike> spaceLikes = new ArrayList<>();

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "user")
    private final List<CommentLike> commentLikes = new ArrayList<>();

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "user")
    private final List<PostLike> postLikes = new ArrayList<>();

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    private String phoneNumber;

    @Setter
    private LocalDate birthOfDate;

    @Setter
    @Enumerated(EnumType.STRING)
    private IdentityType identity;

    @Setter
    @Column(nullable = false)
    private String email;

    @Setter
    @Lob
    private byte[] profilePhoto;

    protected User(String name, String phoneNumber, LocalDate birthOfDate, IdentityType identity, String email, byte[] profilePhoto){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthOfDate = birthOfDate;
        this.identity = identity;
        this.email = email;
        this.profilePhoto = profilePhoto;
        this.refreshToken = RefreshToken.builder().build(); // 빈 값인 RefreshToken 엔티티 생성
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setUser(this);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setUser(null);
    }

    /**
     * User - Schedule 연관관계 편의 메서드(생성)
     */
    public void addSchedule(Schedule schedule) {
        this.schedules.add(schedule);
        schedule.setUser(this);
    }

    /**
     * User - Schedule 연관관계 편의 메서드(삭제)
     */
    public void removeSchedule(Schedule schedule) {
        this.schedules.remove(schedule);
        schedule.setUser(null);
    }

    /**
     * User - Post 연관관계 편의 메서드(생성)
     */
    public void addPost(Post post) {
        this.posts.add(post);
        post.setUser(this);
    }

    /**
     * User - Post 연관관계 편의 메서드(삭제)
     */
    public void removePost(Post post) {
        this.posts.remove(post);
        post.setUser(null);
    }

}
