package success.planfit.post.dto.response;

import lombok.Builder;
import lombok.Getter;
import success.planfit.comment.dto.CommentInfoDto;
import success.planfit.course.dto.SpaceResponseDto;
import success.planfit.entity.comment.Comment;
import success.planfit.entity.post.Post;
import success.planfit.global.photo.PhotoProvider;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostInfoDto {
    private String username;
    private String userTitlePhoto;
    private String title;
    private String location;
    private String content;
    private Boolean isPublic;
    private List<SpaceResponseDto> spaces;
    private List<String> postPhotos;
    private LocalDateTime createdAt;
    private List<CommentInfoDto> comments;


    // 여기서는 진짜 post -> dto space -> spacedto
    public static PostInfoDto from(Post post) {
        // List<Space> -> List<SpaceResponseDto>
        List<SpaceResponseDto> spaces = post.getCourse().getSpaces().stream()
                .map(SpaceResponseDto::createSpaceDto)
                .toList();

        // List<PostPhoto> -> List<String>
        List<String> postPhotos = post.getPostPhotos().stream()
                .map(postPhoto -> PhotoProvider.encode(postPhoto.getPhoto()))
                .toList();

        PostInfoDto postInfoDto = PostInfoDto.builder()
                .username(post.getUser().getName())
                .userTitlePhoto(PhotoProvider.encode(post.getUser().getProfilePhoto()))
                .title(post.getTitle())
                .location(post.getCourse().getLocation())
                .content(post.getContent())
                .isPublic(post.getIsPublic())
                .spaces(spaces)
                .postPhotos(postPhotos)
                .createdAt(post.getCreatedAt())
                .build();

        // List<Comment> -> List<CommentDto>
        for (Comment comment : post.getComments()) {
            CommentInfoDto dto = CommentInfoDto.from(comment);
            postInfoDto.comments.add(dto);
        }
        return postInfoDto;
    }

}
