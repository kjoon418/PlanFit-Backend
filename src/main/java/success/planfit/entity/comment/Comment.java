package success.planfit.entity.comment;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import success.planfit.entity.post.Post;
import success.planfit.entity.user.User;
import success.planfit.global.BaseEntity;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Setter
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long likeCount;

    @Builder
    private Comment(
            Post post,
            User user,
            String content
    ){
        this.post = post;
        this.user = user;
        this.content = content;
        this.likeCount = 0L;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount < 0) {
            throw new IllegalStateException("좋아요를 취소할 수 없음");
        }
        this.likeCount--;
    }

}
