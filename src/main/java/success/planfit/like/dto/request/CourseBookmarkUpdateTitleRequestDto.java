package success.planfit.like.dto.request;

import lombok.Getter;
import success.planfit.global.photo.PhotoType;

@Getter
public class CourseBookmarkUpdateTitleRequestDto {

    private String title;
    private String titlePhoto;
    private PhotoType photoType;

}
