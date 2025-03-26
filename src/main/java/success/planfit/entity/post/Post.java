package success.planfit.entity.post;

import jakarta.persistence.*;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;
import static jakarta.persistence.GenerationType.IDENTITY;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import success.planfit.entity.comment.Comment;
import success.planfit.entity.course.Course;
import success.planfit.entity.user.User;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private Course course;

    @Setter
    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "post")
    List<Comment> comments = new ArrayList<>();

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false)
    private Long likeCount;

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "post")
    List<PostPhoto> postPhotoList = new ArrayList<>();

    @Builder
    private Post(
            Course course,
            User user,
            String content,
            String title,
            Boolean isPublic
    ) {
        this.course = course;
        this.user = user;
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.likeCount = 0L;
    }

    public void addComment(Comment comment){
        comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(Comment comment){
        comments.remove(comment);
        comment.setPost(null);
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void addPostPhoto(PostPhoto postPhoto){
        postPhotoList.add(postPhoto);
        postPhoto.setPost(this);
    }

}
