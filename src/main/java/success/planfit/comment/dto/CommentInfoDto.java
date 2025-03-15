package success.planfit.comment.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CommentInfoDto {

    private String username;
    private String userPhoto;
    private String content;
    private LocalDateTime createdAt;
    private int likeCount;
}
