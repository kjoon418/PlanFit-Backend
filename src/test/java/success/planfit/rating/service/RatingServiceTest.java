package success.planfit.rating.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.entity.course.Course;
import success.planfit.entity.rating.Rating;
import success.planfit.entity.schedule.Schedule;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.user.User;
import success.planfit.fixture.CourseFixture;
import success.planfit.fixture.ScheduleFixture;
import success.planfit.fixture.SpaceDetailFixture;
import success.planfit.fixture.UserFixture;
import success.planfit.rating.dto.RatingInfoResponseDto;
import success.planfit.rating.dto.RatingRecordRequestDto;
import success.planfit.repository.RatingRepository;
import success.planfit.repository.SpaceDetailRepository;
import success.planfit.repository.UserRepository;
import success.planfit.schedule.dto.response.ScheduleResponseDto;
import success.planfit.util.EqualityChecker;
import success.planfit.util.TestUtil;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class RatingServiceTest {

    private static final int DEFAULT_RATING_VALUE = 3;
    private static final LocalDate DEFAULT_DATE = LocalDate.of(9999, 12, 30);

    @Autowired
    private EntityManager em;
    @Autowired
    private TestUtil util;
    @Autowired
    private EqualityChecker equalityChecker;

    @Autowired
    private RatingService ratingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SpaceDetailRepository spaceDetailRepository;
    @Autowired
    private RatingRepository ratingRepository;

    private User user;
    private Schedule schedule;
    private Course course;
    private List<SpaceDetail> spaceDetails;

    @BeforeEach
    void initialize() {
        user = UserFixture.USER_A.createInstance();
        schedule = ScheduleFixture.SCHEDULE_A.createInstance();
        course = CourseFixture.COURSE_A.createInstance();
        spaceDetails = SpaceDetailFixture.createInstances();

        util.connectEntities(user, schedule, course, spaceDetails);
        saveEntities(user, spaceDetails);
    }

    @AfterEach
    void clear() {
        util.clearEntityManager(em);
    }

    @Nested
    class recordRating {

        @ParameterizedTest
        @DisplayName("해당 유저가 방문한 코스 내 모든 장소들에 대한 별점을 기록한다")
        @ValueSource(ints = {1, 2, 3, 4, 5})
        void recordRatingToSpacesInCourse(int ratingValue) {
            ratingService.recordRating(user.getId(), new RatingRecordRequestDto(ratingValue, schedule.getId()));

            List<Rating> ratings = findRatingsBySpaceDetails(spaceDetails);
            for (Rating rating : ratings) {
                assertThat(rating.getValue()).isEqualTo(ratingValue);
            }
        }

        @ParameterizedTest
        @DisplayName("이미 별점을 등록한 적 있는 일정에 또 별점을 등록하려 하면 예외가 발생한다")
        @CsvSource(
                value = {
                        "1,1", "1,2", "1,3,", "1,4", "1,5",
                        "2,1", "2,2", "2,3,", "2,4", "2,5",
                        "3,1", "3,2", "3,3,", "3,4", "3,5",
                        "4,1", "4,2", "4,3,", "4,4", "4,5",
                        "5,1", "5,2", "5,3,", "5,4", "5,5",
                },
                delimiter = ','
        )
        void ifRatingDuplicatedThenThrowException(int prevRating, int nextRating) {
            ratingService.recordRating(user.getId(), new RatingRecordRequestDto(prevRating, schedule.getId()));

            assertThatThrownBy(() -> {
                ratingService.recordRating(user.getId(), new RatingRecordRequestDto(nextRating, schedule.getId()));
                em.flush();
            }).isInstanceOf(ConstraintViolationException.class);

            em.clear();
        }

        @ParameterizedTest
        @DisplayName("별점이 최소치보다 작을 경우 예외가 발생한다")
        @ValueSource(ints = {Integer.MIN_VALUE, -1000, -10, 0})
        void ifRatingValueLessThanMinimumThenThrowException(int illegalValue) {
            assertThatThrownBy(() -> ratingService.recordRating(user.getId(), new RatingRecordRequestDto(illegalValue, schedule.getId())))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @DisplayName("별점이 최대치보다 클 경우 예외가 발생한다")
        @ValueSource(ints = {6, 10, 100, Integer.MAX_VALUE})
        void ifRatingValueBiggerThanMaximumThenThrowException(int illegalValue) {
            assertThatThrownBy(() -> ratingService.recordRating(user.getId(), new RatingRecordRequestDto(illegalValue, schedule.getId())))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

    @Nested
    class getRatingRequestAvailableSchedule {

        @Test
        @DisplayName("별점 요청이 가능한 일정에 대한 정보를 반환한다")
        void returnRatingRequestAvailableSchedule() {
            ScheduleResponseDto scheduleDto = ratingService.getRatingRequestAvailableSchedule(user.getId(), DEFAULT_DATE);

            assertThat(scheduleDto.getScheduleId()).isEqualTo(schedule.getId());
        }

        @Test
        @DisplayName("반환된 일정에 별점 등록이 요청되었음을 반영한다")
        void recordRatingRequested() {
            assertThat(schedule.getRatingRequested()).isFalse();

            ratingService.getRatingRequestAvailableSchedule(user.getId(), DEFAULT_DATE);

            assertThat(schedule.getRatingRequested()).isTrue();
        }

        @Test
        @DisplayName("반환 가능한 일정이 없을 경우 예외가 발생한다")
        void ifNotFoundAvailableScheduleThenThrowException() {
            ratingService.getRatingRequestAvailableSchedule(user.getId(), DEFAULT_DATE); //

            assertThatThrownBy(() -> ratingService.getRatingRequestAvailableSchedule(user.getId(), DEFAULT_DATE))
                    .isInstanceOf(EntityNotFoundException.class);
        }

    }

    @Nested
    class getRatings {

        @Test
        @DisplayName("사용자가 별점을 남긴 일정 정보와 별점 값을 반환한다")
        void returnRatingValueWithScheduleInfo() {
            recordRating(user, schedule, DEFAULT_RATING_VALUE);

            List<RatingInfoResponseDto> responseDtos = ratingService.getRatings(user.getId());
            RatingInfoResponseDto responseDto = responseDtos.getFirst();

            assertThat(equalityChecker.check(schedule, responseDto)).isTrue();
        }

        @Test
        @DisplayName("사용자가 별점을 남긴 일정이 없으면 빈 리스트를 반환한다")
        void ifNotExistRatedScheduleThenReturnEmptyCollection() {
            List<RatingInfoResponseDto> responseDtos = ratingService.getRatings(user.getId());

            assertThat(responseDtos.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("전체 일정 중 사용자가 별점을 남긴 일정만을 조회한다")
        void notReturnNotRatedSchedule() {
            Schedule ratedSchedule = createSchedule(user, ScheduleFixture.SCHEDULE_B);
            Schedule unratedSchedule = createSchedule(user, ScheduleFixture.SCHEDULE_C);
            recordRating(user, ratedSchedule, DEFAULT_RATING_VALUE);

            List<RatingInfoResponseDto> responseDtos = ratingService.getRatings(user.getId());
            assertThat(responseDtos.size()).isEqualTo(1);

            RatingInfoResponseDto responseDto = responseDtos.getFirst();
            assertThat(equalityChecker.check(ratedSchedule, responseDto)).isTrue();
            assertThat(equalityChecker.check(unratedSchedule, responseDto)).isFalse();
        }

    }

    @Nested
    class removeRating {

        @Test
        @DisplayName("해당 유저의 일정 하나에 대한 Rating 엔티티를 제거한다")
        void removeRatingInScheduleOfUser() {
            recordRating(user, schedule, DEFAULT_RATING_VALUE);
            System.out.println("user.getId() = " + user.getId());
            System.out.println("schedule.getId() = " + schedule.getId());

            ratingService.removeRating(user.getId(), schedule.getId());

            List<Rating> ratings = schedule.getRatings();
            assertThat(ratings.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("해당 유저의 일정에 대한 별점 정보가 없어도 예외가 발생하지 않고 넘어간다")
        void ifRatingNotExistThenThrowException() {
            ratingService.removeRating(user.getId(), schedule.getId());

            assertThatCode(() -> ratingService.removeRating(user.getId(), schedule.getId()))
                    .doesNotThrowAnyException();
        }

    }

    private List<Rating> findRatingsBySpaceDetails(List<SpaceDetail> spaceDetails) {
        return ratingRepository.findAll().stream()
                .filter(rating -> spaceDetails.contains(rating.getSpaceDetail()))
                .toList();
    }

    private void saveEntities(User user, List<SpaceDetail> spaceDetails) {
        spaceDetailRepository.saveAll(spaceDetails);
        userRepository.save(user);
    }

    private void recordRating(User user, Schedule schedule, int ratingValue) {
        ratingService.recordRating(user.getId(), new RatingRecordRequestDto(ratingValue, schedule.getId()));
    }

    private Schedule createSchedule(User user, ScheduleFixture scheduleFixture) {
        Schedule schedule = scheduleFixture.createInstance();
        Course course = CourseFixture.DEFAULT_COURSE.createInstance();

        util.connectEntities(user, schedule, course, spaceDetails);
        em.persist(schedule);

        return schedule;
    }

}
