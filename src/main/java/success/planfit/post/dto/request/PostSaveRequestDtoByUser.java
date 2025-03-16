package success.planfit.post.dto.request;


import lombok.Getter;
import success.planfit.dto.request.SpaceResponseDto;

import java.util.List;

@Getter
public class PostSaveRequestDtoByUser {

    private List<String> photoList;
    private String title;
    private String location;
    private String titlePhoto;
    private String content;
    private List<SpaceResponseDto> spaceList;



}
