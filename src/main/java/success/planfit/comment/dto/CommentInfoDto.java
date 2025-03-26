package success.planfit.comment.dto;

import lombok.Builder;
import success.planfit.entity.comment.Comment;
import success.planfit.entity.user.User;
import success.planfit.global.photo.PhotoProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public class CommentInfoDto {

    private String username;
    private String userPhoto;
    private String content;
    private LocalDateTime createdAt;
    private Long likeCount;

    public static CommentInfoDto toDto(User user, Comment comment) {
        return CommentInfoDto.builder()
                .username(user.getName())
                .userPhoto(PhotoProvider.encode(user.getProfilePhoto()))
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .likeCount(comment.getLikeCount())
                .build();
    }

}
