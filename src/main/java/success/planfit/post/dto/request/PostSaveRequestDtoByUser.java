package success.planfit.post.dto.request;


import lombok.Getter;
import success.planfit.course.dto.SpaceRequestDto;

import java.util.List;

@Getter
public class PostSaveRequestDtoByUser {

    private List<String> postPhotoList;
    private String title;
    private String location;
    private String content;
    private Boolean isPublic;
    private List<SpaceRequestDto> spaceList;

}
