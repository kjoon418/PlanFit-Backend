package success.planfit.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import success.planfit.entity.course.Course;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CourseResponseDto {

    private final Long courseId;
    private final String location;
    private final List<SpaceResponseDto> spaces;

    public static CourseResponseDto from(Course course) {
        List<SpaceResponseDto> spaces = course.getSpaces().stream()
                .map(SpaceResponseDto::createSpaceDto)
                .toList();

        return CourseResponseDto.builder()
                .courseId(course.getId())
                .location(course.getLocation())
                .spaces(spaces)
                .build();
    }

}
