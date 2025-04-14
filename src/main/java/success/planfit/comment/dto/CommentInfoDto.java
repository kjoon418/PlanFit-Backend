package success.planfit.comment.dto;

import lombok.Builder;
import success.planfit.entity.comment.Comment;
import success.planfit.global.photo.PhotoProvider;

import java.time.LocalDateTime;

@Builder
public class CommentInfoDto {

    private String username;
    private String userPhoto;
    private String content;
    private LocalDateTime createdAt;
    private Long likeCount;

    public static CommentInfoDto of(Comment comment) {
        return CommentInfoDto.builder()
                .username(comment.getUser().getName())
                .userPhoto(PhotoProvider.encode(comment.getUser().getProfilePhoto()))
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .likeCount(comment.getLikeCount())
                .build();
    }

}
