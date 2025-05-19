package success.planfit.comment.dto;

import lombok.Builder;
import lombok.Getter;
import success.planfit.entity.comment.Comment;
import success.planfit.global.photo.PhotoProvider;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentInfoDto {

    private String username;
    private String userPhoto;
    private String content;
    private LocalDateTime createdAt;
    private Long likeCount;

    public static CommentInfoDto from(Comment comment) {
        return CommentInfoDto.builder()
                .username(comment.getUser().getName())
                .userPhoto(PhotoProvider.encode(comment.getUser().getProfilePhoto()))
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .likeCount(comment.getLikeCount())
                .build();
    }

}
