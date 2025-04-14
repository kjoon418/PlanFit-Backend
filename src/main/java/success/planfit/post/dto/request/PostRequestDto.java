package success.planfit.post.dto.request;


import lombok.Getter;
import success.planfit.course.dto.SpaceRequestDto;

import java.util.List;

@Getter
public class PostRequestDto {
    private String title;
    private String location;
    private String content;
    private Boolean isPublic;
    private List<String> postPhotos;
    private List<SpaceRequestDto> spaces;
    private List<String> postTypes;
}
