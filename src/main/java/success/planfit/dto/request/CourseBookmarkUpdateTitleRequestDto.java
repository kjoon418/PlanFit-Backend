package success.planfit.dto.request;

import lombok.Getter;
import success.planfit.photo.PhotoType;

@Getter
public class CourseBookmarkUpdateTitleRequestDto {

    private String title;
    private String titlePhoto;
    private PhotoType photoType;

}
