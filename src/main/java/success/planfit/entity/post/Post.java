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
import success.planfit.post.dto.request.PostRequestDto;


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

    @Setter
    @OneToOne(fetch = LAZY, cascade = ALL)
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
    private long likeCount;

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "post")
    List<PostPhoto> postPhotos = new ArrayList<>();

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "post")
    List<PostType> postTypes = new ArrayList<>();

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

    public void update(PostRequestDto requestDto){
        this.content = requestDto.getContent();
        this.title = requestDto.getTitle();
        this.isPublic = requestDto.getIsPublic();
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

    /**
     * Post - PostPhoto 연관관계 편의 메서드(전체 생성)
     */
    public void addPostPhotos(List<PostPhoto> postPhotos) {
        for (PostPhoto postPhoto : postPhotos) {
            postPhoto.setPost(this);
        }

        this.postPhotos.addAll(postPhotos);
    }

    /**
     * Post - PostPhoto 연관관계 편의 메서드(전체 삭제)
     */
    public void removeEveryPostPhotos() {
        for (PostPhoto postPhoto : postPhotos) {
            postPhoto.setPost(null);
        }
        postPhotos.clear();
    }

    /**
     * Post - PostType 연관관계 편의 메서드(전체 생성)
     */
    public void addPostTypes(List<PostType> postTypes) {
        for (PostType postType : postTypes) {
            postType.setPost(this);
        }

        this.postTypes.addAll(postTypes);
    }

    /**
     * Post - PostType 연관관계 편의 메서드(전체 삭제)
     */
    public void removeEveryPostTypes() {
        for (PostType postType : postTypes) {
            postType.setPost(null);
        }
        postTypes.clear();
    }
}
