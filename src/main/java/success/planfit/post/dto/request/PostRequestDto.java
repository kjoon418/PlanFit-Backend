package success.planfit.post.dto.request;


import lombok.Getter;
import success.planfit.course.dto.SpaceRequestDto;
import success.planfit.global.validation.NotEmptyAndNotBlank;

import java.util.List;

@Getter
public class PostRequestDto {
    @NotEmptyAndNotBlank("title")
    private String title;

    @NotEmptyAndNotBlank("location")
    private String location;

    @NotEmptyAndNotBlank("content")
    private String content;

    @NotEmptyAndNotBlank("isPublic")
    private Boolean isPublic;

    @NotEmptyAndNotBlank("postPhotos")
    private List<String> postPhotos;

    @NotEmptyAndNotBlank("spaces")
    private List<SpaceRequestDto> spaces;

    @NotEmptyAndNotBlank("postTypes")
    private List<String> postTypes;
}
