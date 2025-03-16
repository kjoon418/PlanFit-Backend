package success.planfit.comment.dto;

import lombok.Getter;

@Getter
public class CommentSaveRequestDto {
    private Long postId;
    private String content;
}
