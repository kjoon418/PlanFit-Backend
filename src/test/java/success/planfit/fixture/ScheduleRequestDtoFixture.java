package success.planfit.fixture;

import success.planfit.course.dto.CourseRequestDto;
import success.planfit.course.dto.SpaceRequestDto;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.schedule.dto.request.ScheduleRequestDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public enum ScheduleRequestDtoFixture {

    DTO_A(
            "A_TITLE",
            LocalDate.of(2022, 4, 6),
            LocalTime.of(12, 30),
            "A_CONTENT",
            "A_LOCATION"
    ),
    DTO_B(
            "B_TITLE",
            LocalDate.of(2022, 4, 6),
            LocalTime.of(12, 30),
            "B_CONTENT",
            "B_LOCATION"
    ),
    DTO_FOR_PAST_SCHEDULE(
            "PAST_TITLE",
            getPastLocalDate(LocalDate.now()),
            LocalTime.of(6, 30),
            "PAST_CONTENT",
            "PAST_LOCATION"
    ),
    DTO_FOR_UPCOMING_SCHEDULE(
            "UPCOMING_TITLE",
            getFutureLocalDate(LocalDate.now()),
            LocalTime.of(6, 30),
            "UPCOMING_CONTENT",
            "UPCOMING_LOCATION"
    );

    private static final int DAY_FOR_DATE_OPERATION = 1;

    private final String title;
    private final LocalDate date;
    private final LocalTime startTime;
    private final String content;
    private final String location;

    ScheduleRequestDtoFixture(
            String title,
            LocalDate date,
            LocalTime startTime,
            String content,
            String location
    ) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.content = content;
        this.location = location;
    }

    public ScheduleRequestDto createInstanceWith(List<SpaceDetail> spaceDetails) {
        List<SpaceRequestDto> spaceRequestDtos = createSpaceRequestDtos(spaceDetails);
        CourseRequestDto courseRequestDto = createCourseRequestDto(location, spaceRequestDtos);

        return createScheduleRequestDto(title, date, startTime, content, courseRequestDto);
    }

    private List<SpaceRequestDto> createSpaceRequestDtos(List<SpaceDetail> spaceDetails) {
        return spaceDetails.stream()
                .map(SpaceDetail::getGooglePlacesIdentifier)
                .map(SpaceRequestDto::new)
                .toList();
    }

    private CourseRequestDto createCourseRequestDto(String location, List<SpaceRequestDto> spaceRequestDtos) {
        return CourseRequestDto.builder()
                .location(location)
                .spaces(spaceRequestDtos)
                .build();
    }

    private ScheduleRequestDto createScheduleRequestDto(
            String title,
            LocalDate date,
            LocalTime startTime,
            String content,
            CourseRequestDto courseRequestDto
    ) {
        return ScheduleRequestDto.builder()
                .title(title)
                .date(date)
                .startTime(startTime)
                .content(content)
                .course(courseRequestDto)
                .build();
    }

    private static LocalDate getPastLocalDate(LocalDate referenceDate) {
        return referenceDate.minusDays(DAY_FOR_DATE_OPERATION);
    }

    private static LocalDate getFutureLocalDate(LocalDate referenceDate) {
        return referenceDate.plusDays(DAY_FOR_DATE_OPERATION);
    }

}
