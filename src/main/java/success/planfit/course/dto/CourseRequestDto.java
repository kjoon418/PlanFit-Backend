package success.planfit.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CourseRequestDto {

    private final String location;
    private final List<SpaceRequestDto> spaces;

}
