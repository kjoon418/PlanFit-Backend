package success.planfit.schedule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.course.dto.CourseRequestDto;
import success.planfit.course.dto.SpaceRequestDto;
import success.planfit.entity.course.Course;
import success.planfit.entity.schedule.Schedule;
import success.planfit.entity.space.Space;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.user.User;
import success.planfit.global.exception.IllegalRequestException;
import success.planfit.repository.ScheduleRepository;
import success.planfit.repository.SpaceDetailRepository;
import success.planfit.repository.UserRepository;
import success.planfit.schedule.dto.ShareSerialDto;
import success.planfit.schedule.dto.request.ScheduleCurrentSequenceUpdateRequestDto;
import success.planfit.schedule.dto.request.ScheduleRequestDto;
import success.planfit.schedule.dto.response.ScheduleResponseDto;
import success.planfit.schedule.dto.response.ScheduleTitleInfoResponseDto;
import success.planfit.schedule.util.ShareSerialGenerator;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ScheduleService {

    private static final Supplier<EntityNotFoundException> USER_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("유저 조회에 실패했습니다.");
    private static final Supplier<EntityNotFoundException> SPACE_DETAIL_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("SpaceDetail이 존재하지 않습니다.");
    private static final Supplier<EntityNotFoundException> SCHEDULE_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("해당 ID를 지닌 일정을 찾을 수 없습니다.");
    private static final Supplier<EntityNotFoundException> SHARE_SERIAL_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("해당 Share serial을 지닌 일정을 찾을 수 없습니다.");

    private final UserRepository userRepository;
    private final SpaceDetailRepository spaceDetailRepository;
    private final ShareSerialGenerator shareSerialGenerator;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void registerSchedule(Long userId, ScheduleRequestDto requestDto) {
        User user = findUserWithSchedules(userId);

        Schedule schedule = createSchedule(requestDto);
        Course course = createCourse(requestDto.getCourse());
        List<Space> spaces = createSpaces(requestDto.getCourse());

        connectEntities(user, schedule, course, spaces);
    }

    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        User user = findUserWithSchedules(userId);
        Schedule schedule = findScheduleById(user, scheduleId);

        user.removeSchedule(schedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleTitleInfoResponseDto> findPastSchedules(Long userId, LocalDate referenceDate) {
        User user = findUserWithSchedules(userId);

        List<Schedule> pastSchedules = getPastSchedules(user, referenceDate);

        return pastSchedules.stream()
                .sorted(Comparator.reverseOrder())
                .map(ScheduleTitleInfoResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleTitleInfoResponseDto> findUpcomingSchedules(Long userId, LocalDate referenceDate) {
        User user = findUserWithSchedules(userId);

        List<Schedule> upcomingSchedules = getUpcomingSchedules(user, referenceDate);

        return upcomingSchedules.stream()
                .sorted()
                .map(ScheduleTitleInfoResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto findScheduleDetail(Long userId, Long scheduleId) {
        User user = findUserWithSchedules(userId);
        Schedule schedule = findScheduleById(user, scheduleId);

        return ScheduleResponseDto.from(schedule);
    }

    @Transactional
    public void update(Long userId, Long scheduleId, ScheduleRequestDto requestDto) {
        User user = findUserWithSchedules(userId);
        Schedule schedule = findScheduleById(user, scheduleId);
        Course course = schedule.getCourse();
        List<Space> spaces = createSpaces(requestDto.getCourse());

        updateSchedule(schedule, requestDto);
        updateCourse(course, requestDto.getCourse());
        replaceSpaces(course, spaces);
    }

    @Transactional
    public void updateCurrentSequence(Long userId, ScheduleCurrentSequenceUpdateRequestDto requestDto) {
        User user = findUserWithSchedules(userId);
        Schedule schedule = findScheduleById(user, requestDto.getScheduleId());

        validateCurrentSequence(schedule, requestDto.getSequence());

        schedule.setCurrentSequence(requestDto.getSequence());
    }

    @Transactional
    public ShareSerialDto createShareSerial(Long userId, Long scheduleId) {
        User user = findUserWithSchedules(userId);
        Schedule schedule = findScheduleById(user, scheduleId);

        Set<String> existsShareSerials = getExistsShareSerials();
        String shareSerial = shareSerialGenerator.generateUniqueSerial(existsShareSerials);

        schedule.setShareSerial(shareSerial);

        return ShareSerialDto.builder()
                .shareSerial(shareSerial)
                .build();
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto findByShareSerial(String shareSerial) {
        Schedule schedule = scheduleRepository.findByShareSerial(shareSerial)
                .orElseThrow(SHARE_SERIAL_NOT_FOUND_EXCEPTION);

        return ScheduleResponseDto.from(schedule);
    }

    private User findUserWithSchedules(Long userId) {
        return userRepository.findByIdWithSchedule(userId)
                .orElseThrow(USER_NOT_FOUND_EXCEPTION);
    }

    private Course createCourse(CourseRequestDto requestDto) {
        return Course.builder()
                .location(requestDto.getLocation())
                .build();
    }

    private List<Space> createSpaces(CourseRequestDto requestDto) {
        ArrayList<Space> spaces = new ArrayList<>();

        int sequence = 0;
        for (SpaceRequestDto spaceRequestDto : requestDto.getSpaces()) {
            SpaceDetail spaceDetail = spaceDetailRepository.findByGooglePlacesIdentifier(spaceRequestDto.getGooglePlacesIdentifier())
                    .orElseThrow(SPACE_DETAIL_NOT_FOUND_EXCEPTION);
            spaces.add(createSpace(spaceDetail, sequence));

            sequence++;
        }

        return Collections.unmodifiableList(spaces);
    }

    private Space createSpace(SpaceDetail spaceDetail, int sequence) {
        return Space.builder()
                .sequence(sequence)
                .spaceDetail(spaceDetail)
                .build();
    }

    private Schedule createSchedule(ScheduleRequestDto requestDto) {
        return Schedule.builder()
                .title(requestDto.getTitle())
                .date(requestDto.getDate())
                .content(requestDto.getContent())
                .startTime(requestDto.getStartTime())
                .build();
    }

    private void connectEntities(User user, Schedule schedule, Course course, List<Space> spaces) {
        user.addSchedule(schedule);
        schedule.setCourse(course);
        course.addSpaces(spaces);
    }

    private Schedule findScheduleById(User user, Long scheduleId) {
        return user.getSchedules().stream()
                .filter(schedule -> schedule.getId().equals(scheduleId))
                .findAny()
                .orElseThrow(SCHEDULE_NOT_FOUND_EXCEPTION);
    }

    private List<Schedule> getPastSchedules(User user, LocalDate criteriaDate) {
        return user.getSchedules().stream()
                .filter(schedule -> schedule.getDate().isBefore(criteriaDate))
                .toList();
    }

    private List<Schedule> getUpcomingSchedules(User user, LocalDate criteriaDate) {
        return user.getSchedules().stream()
                .filter(schedule -> schedule.getDate().isAfter(criteriaDate))
                .toList();
    }

    private void updateSchedule(Schedule schedule, ScheduleRequestDto requestDto) {
        schedule.setTitle(requestDto.getTitle());
        schedule.setDate(requestDto.getDate());
        schedule.setStartTime(requestDto.getStartTime());
        schedule.setContent(requestDto.getContent());
        schedule.setCurrentSequence(0);
    }

    private void updateCourse(Course course, CourseRequestDto requestDto) {
        course.setLocation(requestDto.getLocation());
    }

    private void replaceSpaces(Course course, List<Space> spaces) {
        course.removeEverySpace();
        course.addSpaces(spaces);
    }

    private void validateCurrentSequence(Schedule schedule, int currentSequence) {
        int spacesSize = schedule.getCourse()
                .getSpaces()
                .size();

        if (currentSequence > spacesSize) {
            throw new IllegalRequestException("일정의 순서는 장소 개수보다 클 수 없습니다.");
        }
    }

    private Set<String> getExistsShareSerials() {
        return scheduleRepository.findAll().stream()
                .map(Schedule::getShareSerial)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

}
