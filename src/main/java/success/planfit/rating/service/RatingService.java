package success.planfit.rating.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.entity.rating.Rating;
import success.planfit.entity.schedule.Schedule;
import success.planfit.entity.space.Space;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.user.User;
import success.planfit.rating.dto.RatingInfoResponseDto;
import success.planfit.rating.dto.RatingRecordRequestDto;
import success.planfit.repository.UserRepository;
import success.planfit.schedule.dto.response.ScheduleResponseDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RatingService {

    private static final Supplier<EntityNotFoundException> USER_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("해당 ID를 통해 유저를 조회할 수 없습니다.");
    private static final Supplier<EntityNotFoundException> SCHEDULE_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("해당 ID를 통해 일정을 조회할 수 없습니다.");
    private static final Supplier<EntityNotFoundException> RATING_REQUEST_AVAILABLE_SCHEDULE_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("별점 요청을 보낼 수 있는 코스가 존재하지 않습니다.");

    private final UserRepository userRepository;

    @Transactional
    public void recordRating(long userId, RatingRecordRequestDto requestDto) {
        User user = getUserWithSchedule(userId);
        Schedule schedule = getSchedule(user, requestDto.scheduleId());
        List<SpaceDetail> spaceDetails = getSpaceDetails(schedule);

        for (SpaceDetail spaceDetail : spaceDetails) {
            Rating rating = createRating(schedule, spaceDetail, requestDto);
            connectEntities(schedule, spaceDetail, rating);
        }
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto getRatingRequestAvailableSchedule(long userId, LocalDate date) {
        User user = getUserWithSchedule(userId);

        Schedule ratingRequestAvailableSchedules = user.getSchedules().stream()
                .filter(isOutdated(date))
                .filter(hasNotRequested())
                .sorted()
                .findFirst()
                .orElseThrow(RATING_REQUEST_AVAILABLE_SCHEDULE_NOT_FOUND_EXCEPTION);

        ratingRequestAvailableSchedules.recordRatingRequest();

        return ScheduleResponseDto.from(ratingRequestAvailableSchedules);
    }

    @Transactional(readOnly = true)
    public List<RatingInfoResponseDto> getRatings(long userId) {
        User user = getUserWithSchedule(userId);
        List<Schedule> schedules = user.getSchedules();

        List<RatingInfoResponseDto> responseDtos = new ArrayList<>();
        for (Schedule schedule : schedules) {
            System.out.println("schedule = " + schedule);

            if (schedule.getRatings().isEmpty()) {
                continue;
            }

            Rating rating = schedule.getRatings().getFirst();
            responseDtos.add(RatingInfoResponseDto.from(schedule, rating));
        }

        return Collections.unmodifiableList(responseDtos);
    }

    @Transactional
    public void removeRating(long userId, long scheduleId) {
        User user = getUserWithSchedule(userId);
        Schedule schedule = findScheduleById(user, scheduleId);

        schedule.clearRatings();
    }

    private User getUserWithSchedule(long userId) {
        return userRepository.findByIdWithSchedule(userId)
                .orElseThrow(USER_NOT_FOUND_EXCEPTION);
    }

    private Schedule getSchedule(User user, long scheduleId) {
        return user.getSchedules().stream()
                .filter(schedule -> schedule.getId().equals(scheduleId))
                .findAny()
                .orElseThrow(SCHEDULE_NOT_FOUND_EXCEPTION);
    }

    private List<SpaceDetail> getSpaceDetails(Schedule schedule) {
        return schedule.getCourse()
                .getSpaces()
                .stream()
                .map(Space::getSpaceDetail)
                .toList();
    }

    private Rating createRating(Schedule schedule, SpaceDetail spaceDetail, RatingRecordRequestDto requestDto) {
        return Rating.builder()
                .schedule(schedule)
                .spaceDetail(spaceDetail)
                .value(requestDto.ratingValue())
                .build();
    }

    private void connectEntities(Schedule schedule, SpaceDetail spaceDetail, Rating rating) {
        schedule.addRating(rating);
        spaceDetail.addRating(rating);
    }

    private Predicate<Schedule> isOutdated(LocalDate date) {
        return schedule -> schedule.getDate().isBefore(date);
    }

    private Predicate<Schedule> hasNotRequested() {
        return schedule -> !schedule.getRatingRequested();
    }

    private Schedule findScheduleById(User user, long scheduleId) {
        return user.getSchedules().stream()
                .filter(schedule -> schedule.getId().equals(scheduleId))
                .findAny()
                .orElseThrow(SCHEDULE_NOT_FOUND_EXCEPTION);
    }

}
