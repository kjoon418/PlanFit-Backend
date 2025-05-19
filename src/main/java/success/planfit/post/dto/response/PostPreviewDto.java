package success.planfit.post.dto.response;

import lombok.Builder;
import lombok.Getter;
import success.planfit.entity.post.Post;
import success.planfit.entity.user.User;
import success.planfit.global.photo.PhotoProvider;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostPreviewDto {
    private String username;
    private String userTitlePhoto;
    private String title;
    private String content;
    private String titlePhoto;
    private LocalDateTime createdAt;

    public static PostPreviewDto of(Post post, User user) {
        return PostPreviewDto.builder()
                .username(user.getName())
                .userTitlePhoto(PhotoProvider.encode(user.getProfilePhoto()))
                .title(post.getTitle())
                .content(post.getContent())
                .titlePhoto(PhotoProvider.encode(post.getPostPhotos().get(0).getPhoto()))
                .createdAt(post.getCreatedAt())
                .build();
    }
}
