package success.planfit.post.dto.response;

import lombok.Builder;
import lombok.Setter;
import success.planfit.comment.dto.CommentInfoDto;
import success.planfit.course.dto.SpaceResponseDto;
import success.planfit.entity.comment.Comment;
import success.planfit.entity.post.Post;
import success.planfit.global.photo.PhotoProvider;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public class PostInfoDto {
    private String username;
    private String userTitlePhoto;
    private String title;
    private String location;
    private String content;
    private Boolean isPublic;
    private List<SpaceResponseDto> spaceList;
    private List<String> postPhotoList;
    private LocalDateTime createdAt;
    @Setter
    private List<CommentInfoDto> commentList;


    // 여기서는 진짜 post -> dto space -> spacedto
    public static PostInfoDto from(Post post) {
        // List<Space> -> List<SpaceResponseDto>
        List<SpaceResponseDto> spaceList = post.getCourse().getSpaces().stream()
                .map(SpaceResponseDto::from)
                .toList();

        // List<PostPhoto> -> List<String>
        List<String> postPhotoList = post.getPostPhotoList().stream()
                .map(postPhoto -> PhotoProvider.encode(postPhoto.getPhoto()))
                .toList();

        PostInfoDto postInfoDto = PostInfoDto.builder()
                .username(post.getUser().getName())
                .userTitlePhoto(PhotoProvider.encode(post.getUser().getProfilePhoto()))
                .title(post.getTitle())
                .location(post.getCourse().getLocation())
                .content(post.getContent())
                .isPublic(post.getIsPublic())
                .spaceList(spaceList)
                .postPhotoList(postPhotoList)
                .createdAt(post.getCreatedAt())
                .build();

        // List<Comment> -> List<CommentDto>
        for (Comment comment : post.getComments()) {
            CommentInfoDto dto = CommentInfoDto.toDto(user, comment);
            postInfoDto.commentList.add(dto);
        }
        return postInfoDto;

    }

}
