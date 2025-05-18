package success.planfit.course.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class CourseRequestDto {

    private final String location;
    private final List<SpaceRequestDto> spaces;

}
