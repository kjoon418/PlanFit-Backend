package success.planfit.rating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.entity.course.Course;
import success.planfit.entity.schedule.Schedule;
import success.planfit.entity.space.Rating;
import success.planfit.entity.space.Space;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.user.User;
import success.planfit.global.exception.EntityNotFoundException;
import success.planfit.rating.dto.RatingRecordRequestDto;
import success.planfit.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RatingService {

    private static final Supplier<EntityNotFoundException> USER_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("해당 ID를 통해 유저를 조회할 수 없습니다.");
    private static final Supplier<EntityNotFoundException> COURSE_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("해당 ID를 통해 코스를 조회할 수 없습니다.");

    private final UserRepository userRepository;

    @Transactional
    public void recordRating(Long userId, RatingRecordRequestDto requestDto) {
        User user = getUser(userId);
        Course course = getCourse(user, requestDto.courseId());
        List<SpaceDetail> spaceDetails = getSpaceDetails(course);

        for (SpaceDetail spaceDetail : spaceDetails) {
            Optional<Rating> existsRating = getExistsRating(user, spaceDetail);

            if (existsRating.isPresent()) {
                Rating rating = existsRating.get();
                updateRating(rating, requestDto);
                continue;
            }

            Rating rating = createRating(user, spaceDetail, requestDto);
            connectEntities(user, spaceDetail, rating);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findByIdWithRatings(userId)
                .orElseThrow(USER_NOT_FOUND_EXCEPTION);
    }

    private Course getCourse(User user, Long courseId) {
        return user.getSchedules().stream()
                .map(Schedule::getCourse)
                .filter(course -> course.getId().equals(courseId))
                .findAny()
                .orElseThrow(COURSE_NOT_FOUND_EXCEPTION);
    }

    private List<SpaceDetail> getSpaceDetails(Course course) {
        return course.getSpaces().stream()
                .map(Space::getSpaceDetail)
                .toList();
    }

    private Optional<Rating> getExistsRating(User user, SpaceDetail spaceDetail) {
        return user.getRatings().stream()
                .filter(rating -> rating.getSpaceDetail().equals(spaceDetail))
                .filter(rating -> rating.getUser().equals(user))
                .findAny();
    }

    private void updateRating(Rating rating, RatingRecordRequestDto requestDto) {
        rating.setValue(requestDto.ratingValue());
    }

    private Rating createRating(User user, SpaceDetail spaceDetail, RatingRecordRequestDto requestDto) {
        return Rating.builder()
                .user(user)
                .spaceDetail(spaceDetail)
                .value(requestDto.ratingValue())
                .build();
    }

    private void connectEntities(User user, SpaceDetail spaceDetail, Rating rating) {
        user.addRating(rating);
        spaceDetail.addRating(rating);
    }

}
