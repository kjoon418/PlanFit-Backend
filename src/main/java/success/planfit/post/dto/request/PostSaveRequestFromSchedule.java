package success.planfit.post.dto.request;

import lombok.Getter;
import success.planfit.course.dto.SpaceResponseDto;

import java.util.List;

@Getter
public class PostSaveRequestFromSchedule {
    private List<String> postPhotoList;
    private String title;
    private String location;
    private String content;
    private Boolean isPublic;
    private Long scheduleId;
}
