package success.planfit.post.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class PostSaveRequestFromSchedule {
    private List<String> postPhotos;
    private String title;
    private String location;
    private String content;
    private Boolean isPublic;
    private Long scheduleId;
}
