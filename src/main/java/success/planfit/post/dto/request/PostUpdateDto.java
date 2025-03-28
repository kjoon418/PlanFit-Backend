package success.planfit.post.dto.request;

import lombok.Getter;
import success.planfit.course.dto.SpaceResponseDto;

import java.util.List;

@Getter
public class PostUpdateDto {
    private List<String> photos;
    private String title;
    private String location;
    private String titlePhoto;
    private String content;
    private Boolean isPublic;
    private List<SpaceResponseDto> spaces;
}
