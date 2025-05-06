package success.planfit.schedule.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.course.dto.CourseRequestDto;
import success.planfit.course.dto.SpaceRequestDto;
import success.planfit.entity.course.Course;
import success.planfit.entity.schedule.Schedule;
import success.planfit.entity.space.Space;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.user.User;
import success.planfit.fixture.*;
import success.planfit.global.exception.IllegalRequestException;
import success.planfit.repository.SpaceDetailRepository;
import success.planfit.repository.UserRepository;
import success.planfit.schedule.dto.ShareSerialDto;
import success.planfit.schedule.dto.request.ScheduleCurrentSequenceUpdateRequestDto;
import success.planfit.schedule.dto.request.ScheduleRequestDto;
import success.planfit.schedule.dto.response.ScheduleResponseDto;
import success.planfit.schedule.dto.response.ScheduleTitleInfoResponseDto;
import success.planfit.util.EqualityChecker;
import success.planfit.util.TestUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ScheduleServiceTest {

    private static final long NOT_EXIST_ID = Long.MAX_VALUE;
    private static final String DEFAULT_SHARE_SERIAL = "23432423958712705589273982357132750";
    private static final String NOT_EXIST_SHARE_SERIAL = "111111111111111111111111111";
    private static final String NOISE_FOR_ANOTHER_SPACE_DETAILS = "NOISE";
    private static final int DEFAULT_CURRENT_SEQUENCE = 1;

    @Autowired
    private TestUtil util;
    @Autowired
    private EqualityChecker equalityChecker;
    @Autowired
    private EntityManager em;

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SpaceDetailRepository spaceDetailRepository;

    private User userA;
    private User userB;
    private User vanillaUser;
    private Schedule scheduleA;
    private Schedule scheduleB;
    private Course courseA;
    private Course courseB;
    private List<SpaceDetail> spaceDetailsA;
    private List<SpaceDetail> spaceDetailsB;
    private ScheduleRequestDto scheduleRequestDto;
    private ScheduleRequestDto pastScheduleRequestDto;
    private ScheduleRequestDto upcomingScheduleRequestDto;

    @BeforeEach
    void initialize() {
        userA = UserFixture.USER_A.createInstance();
        scheduleA = ScheduleFixture.SCHEDULE_A.createInstance();
        courseA = CourseFixture.COURSE_A.createInstance();
        spaceDetailsA = SpaceDetailFixture.createInstances();

        util.connectEntities(userA, scheduleA, courseA, spaceDetailsA);
        saveEntities(userA, spaceDetailsA);

        userB = UserFixture.USER_B.createInstance();
        scheduleB = ScheduleFixture.SCHEDULE_B.createInstance();
        courseB = CourseFixture.COURSE_B.createInstance();
        spaceDetailsB = SpaceDetailFixture.createInstancesWith(NOISE_FOR_ANOTHER_SPACE_DETAILS);

        util.connectEntities(userB, scheduleB, courseB, spaceDetailsB);
        saveEntities(userB, spaceDetailsB);

        scheduleRequestDto = ScheduleRequestDtoFixture.DTO_A.createInstanceWith(spaceDetailsA);
        pastScheduleRequestDto = ScheduleRequestDtoFixture.DTO_FOR_PAST_SCHEDULE.createInstanceWith(spaceDetailsA);
        upcomingScheduleRequestDto = ScheduleRequestDtoFixture.DTO_FOR_UPCOMING_SCHEDULE.createInstanceWith(spaceDetailsA);

        vanillaUser = UserFixture.USER_C.createInstance();
        userRepository.save(vanillaUser);
    }

    @AfterEach
    void clearEntityManager() {
        util.clearEntityManager(em);
    }

    @Nested
    class registerSchedule {

        @Test
        @DisplayName("해당 회원의 일정을 새로 한 개 등록한다")
        void registerScheduleForUser() {
            scheduleService.registerSchedule(vanillaUser.getId(), scheduleRequestDto);

            List<Schedule> schedules = vanillaUser.getSchedules();
            assertThat(schedules.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("DTO의 값과 같은 Schedule 엔티티를 생성해 회원과 연결한다")
        void connectSameScheduleAsDto() {
            scheduleService.registerSchedule(vanillaUser.getId(), scheduleRequestDto);

            Schedule schedule = vanillaUser.getSchedules()
                    .getFirst();

            assertThat(equalityChecker.check(schedule, scheduleRequestDto)).isTrue();
        }

        @Test
        @DisplayName("DTO의 값과 같은 Course 엔티티를 생성해 회원의 Schedule과 연결한다")
        void connectSameCourseAsDto() {
            scheduleService.registerSchedule(vanillaUser.getId(), scheduleRequestDto);

            Schedule schedule = getFirstSchedule(vanillaUser);

            Course course = schedule.getCourse();
            CourseRequestDto courseDto = scheduleRequestDto.getCourse();

            assertThat(equalityChecker.check(course, courseDto)).isTrue();
        }

        @Test
        @DisplayName("DTO 속 장소 정보와 같은 수만큼 Space 엔티티를 생성해 회원의 Schedule 속 Course와 연결한다")
        void connectSameAmountOfSpaceAsDto() {
            scheduleService.registerSchedule(vanillaUser.getId(), scheduleRequestDto);

            List<Space> spaces = getFirstSchedule(vanillaUser)
                    .getCourse()
                    .getSpaces();
            List<SpaceRequestDto> spaceDto = scheduleRequestDto.getCourse()
                    .getSpaces();

            assertThat(spaces.size()).isEqualTo(spaceDto.size());
        }

        @Test
        @DisplayName("DTO와 같은 값과 순서로 Space 엔티티를 생성해 회원의 Schedule 속 Course와 연결한다")
        void connectSameSpaceAsDto() {
            scheduleService.registerSchedule(vanillaUser.getId(), scheduleRequestDto);

            List<Space> spaces = getFirstSchedule(vanillaUser)
                    .getCourse()
                    .getSpaces();
            List<SpaceRequestDto> spaceDtos = scheduleRequestDto.getCourse()
                    .getSpaces();

            List<Space> sortedSpaces = getSpacesSortedBySequence(spaces);
            for (int i = 0; i < sortedSpaces.size(); i++) {
                Space space = sortedSpaces.get(i);
                SpaceRequestDto spaceDto = spaceDtos.get(i);

                assertThat(equalityChecker.check(space, spaceDto)).isTrue();
            }
        }

        @Test
        @DisplayName("존재하지 않는 회원의 ID를 전달하면 예외가 발생한다")
        void ifUseNotExistUserIdThenThrowException() {
            assertThatCode(() -> scheduleService.registerSchedule(NOT_EXIST_ID, scheduleRequestDto))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        private List<Space> getSpacesSortedBySequence(List<Space> spaces) {
            return spaces.stream()
                    .sorted(Comparator.comparingInt(Space::getSequence))
                    .toList();
        }

        private Schedule getFirstSchedule(User user) {
            return user.getSchedules()
                    .getFirst();
        }

    }

    @Nested
    class deleteSchedule {

        @Test
        @DisplayName("해당 회원의 일정을 제거한다")
        void deleteScheduleOfUser() {
            scheduleService.deleteSchedule(userA.getId(), scheduleA.getId());
            util.clearEntityManager(em);

            Schedule findSchedule = em.find(Schedule.class, scheduleA.getId());

            assertThat(findSchedule).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 회원의 ID를 전달하면 예외가 발생한다")
        void ifUseNotExistUserIdThenThrowException() {
            assertThatThrownBy(() -> scheduleService.deleteSchedule(NOT_EXIST_ID, scheduleA.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 일정의 ID를 전달하면 예외가 발생한다")
        void ifUseNotExistScheduleIdThenThrowException() {
            assertThatThrownBy(() -> scheduleService.deleteSchedule(userA.getId(), NOT_EXIST_ID))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("해당 회원의 일정이 아닌 일정의 ID를 전달하면 예외가 발생한다")
        void ifUseScheduleIdOfOtherUserThenThrowException() {
            assertThatThrownBy(() -> scheduleService.deleteSchedule(userA.getId(), scheduleB.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

    }

    @Nested
    class findPastSchedules {

        @ParameterizedTest
        @DisplayName("해당 회원의 지난 일정들을 조회한다")
        @ValueSource(ints = {1, 5, 10, 15})
        void findPastSchedulesByUser(int pastScheduleAmount) {
            for (int i = 0; i < pastScheduleAmount; i++) {
                registerPastSchedule();
                registerUpcomingSchedule();
            }

            List<ScheduleTitleInfoResponseDto> pastScheduleResponseDtos = scheduleService.findPastSchedules(vanillaUser.getId(), LocalDate.now());
            assertThat(pastScheduleResponseDtos.size()).isEqualTo(pastScheduleAmount);

            for (ScheduleTitleInfoResponseDto pastScheduleResponseDto : pastScheduleResponseDtos) {
                assertThat(equalityChecker.check(pastScheduleRequestDto, pastScheduleResponseDto)).isTrue();
            }
        }

        @Test
        @DisplayName("지난 일정이 없을 경우 빈 리스트를 반환한다")
        void ifNotExistPastScheduleThenReturnEmptyList() {
            List<ScheduleTitleInfoResponseDto> pastScheduleResponseDtos = scheduleService.findPastSchedules(vanillaUser.getId(), LocalDate.now());

            assertThat(pastScheduleResponseDtos).isEmpty();
        }

        @Test
        @DisplayName("다가올 일정만 있는 경우 빈 리스트를 반환한다")
        void ifOnlyExistUpcomingScheduleThenReturnEmptyList() {
            registerUpcomingSchedule();

            List<ScheduleTitleInfoResponseDto> pastScheduleResponseDtos = scheduleService.findPastSchedules(vanillaUser.getId(), LocalDate.now());

            assertThat(pastScheduleResponseDtos).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 회원의 ID를 전달하면 예외가 발생한다")
        void ifUseNotExistUserIdThenThrowException() {
            assertThatThrownBy(() -> scheduleService.findPastSchedules(NOT_EXIST_ID, LocalDate.now()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

    }

    @Nested
    class findUpcomingSchedules {

        @ParameterizedTest
        @DisplayName("해당 회원의 다가올 일정들을 조회한다")
        @ValueSource(ints = {1, 5, 10, 15})
        void findUpcomingSchedulesByUser(int upcomingScheduleAmount) {
            for (int i = 0; i < upcomingScheduleAmount; i++) {
                registerPastSchedule();
                registerUpcomingSchedule();
            }

            List<ScheduleTitleInfoResponseDto> upcomingScheduleResponseDtos = scheduleService.findUpcomingSchedules(vanillaUser.getId(), LocalDate.now());
            assertThat(upcomingScheduleResponseDtos.size()).isEqualTo(upcomingScheduleAmount);

            for (ScheduleTitleInfoResponseDto upcomingScheduleResponseDto : upcomingScheduleResponseDtos) {
                assertThat(equalityChecker.check(upcomingScheduleRequestDto, upcomingScheduleResponseDto)).isTrue();
            }
        }

        @Test
        @DisplayName("다가올 일정이 없을 경우 빈 리스트를 반환한다")
        void ifNotExistUpcomingScheduleThenReturnEmptyList() {
            List<ScheduleTitleInfoResponseDto> upcomingScheduleResponseDtos = scheduleService.findUpcomingSchedules(vanillaUser.getId(), LocalDate.now());

            assertThat(upcomingScheduleResponseDtos).isEmpty();
        }

        @Test
        @DisplayName("지난 일정만 있는 경우 빈 리스트를 반환한다")
        void ifOnlyExistPastScheduleThenReturnEmptyList() {
            registerPastSchedule();

            List<ScheduleTitleInfoResponseDto> upcomingScheduleResponseDtos = scheduleService.findUpcomingSchedules(vanillaUser.getId(), LocalDate.now());

            assertThat(upcomingScheduleResponseDtos).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 회원의 ID를 전달하면 예외가 발생한다")
        void ifUseNotExistUserIdThenThrowException() {
            assertThatThrownBy(() -> scheduleService.findUpcomingSchedules(NOT_EXIST_ID, LocalDate.now()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

    }

    @Nested
    class findScheduleDetail {

        @Test
        @DisplayName("해당 유저가 지닌 일정의 세부 정보를 반환한다")
        void returnDetailInformationOfSchedule() {
            ScheduleResponseDto scheduleResponseDto = scheduleService.findScheduleDetail(userA.getId(), scheduleA.getId());

            assertThat(equalityChecker.check(scheduleA, scheduleResponseDto)).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 회원의 ID를 전달하면 예외가 발생한다")
        void ifUseNotExistUserIdThenThrowException() {
            assertThatThrownBy(() -> scheduleService.findScheduleDetail(NOT_EXIST_ID, scheduleA.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 일정의 ID를 전달하면 예외가 발생한다")
        void ifUseNotExistScheduleIdThenThrowException() {
            assertThatThrownBy(() -> scheduleService.findScheduleDetail(userA.getId(), NOT_EXIST_ID))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("다른 회원의 일정 ID를 전달하면 예외가 발생한다")
        void ifUseScheduleIdOfOtherUserThenThrowException() {
            assertThatThrownBy(() -> scheduleService.findScheduleDetail(userA.getId(), scheduleB.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

    }

    @Nested
    class update {

        @Test
        @DisplayName("해당 회원의 일정 정보를 새롭게 덮어쓴다")
        void updateScheduleInformation() {
            scheduleService.update(userA.getId(), scheduleA.getId(), scheduleRequestDto);

            assertThat(equalityChecker.check(scheduleA, scheduleRequestDto)).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 회원의 ID를 전달하면 예외가 발생한다")
        void ifUseNotExistUserIdThenThrowException() {
            assertThatThrownBy(() -> scheduleService.update(NOT_EXIST_ID, scheduleA.getId(), scheduleRequestDto))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 일정의 ID를 전달하면 예외가 발생한다")
        void ifUseNotExistScheduleIdThenThrowException() {
            assertThatThrownBy(() -> scheduleService.update(userA.getId(), NOT_EXIST_ID, scheduleRequestDto))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("다른 회원의 일정 ID를 전달하면 예외가 발생한다")
        void ifUseScheduleIdOfOtherUserThenThrowException() {
            assertThatThrownBy(() -> scheduleService.update(userA.getId(), scheduleB.getId(), scheduleRequestDto))
                    .isInstanceOf(EntityNotFoundException.class);
        }

    }

    @Nested
    class updateCurrentSequence {

        @Test
        @DisplayName("일정의 장소 방문 순서를 수정한다")
        void updateCurrentSequenceOfSchedule() {
            ScheduleCurrentSequenceUpdateRequestDto requestDto = createSequenceUpdateRequestDto(scheduleA.getId(), DEFAULT_CURRENT_SEQUENCE);

            scheduleService.updateCurrentSequence(userA.getId(), requestDto);

            assertThat(scheduleA.getCurrentSequence()).isEqualTo(DEFAULT_CURRENT_SEQUENCE);
        }

        @Test
        @DisplayName("일정의 장소 개수보다 높은 값으로 수정하려 하면 예외가 발생한다")
        void ifSequenceBiggerThanSpaceAmountThenThrowException() {
            int spaceAmount = getSpaceAmount(scheduleA);

            ScheduleCurrentSequenceUpdateRequestDto requestDto = createSequenceUpdateRequestDto(scheduleA.getId(), spaceAmount + 1);

            assertThatThrownBy(() -> scheduleService.updateCurrentSequence(userA.getId(), requestDto))
                    .isInstanceOf(IllegalRequestException.class);
        }

        @Test
        @DisplayName("존재하지 않는 회원의 ID를 전달하면 예외가 발생한다")
        void ifUseNotExistUserIdThenThrowException() {
            ScheduleCurrentSequenceUpdateRequestDto requestDto = createSequenceUpdateRequestDto(scheduleA.getId(), DEFAULT_CURRENT_SEQUENCE);

            assertThatThrownBy(() -> scheduleService.updateCurrentSequence(NOT_EXIST_ID, requestDto))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 일정의 ID를 전달하면 예외가 발생한다")
        void ifUseNotExistScheduleIdThenThrowException() {
            ScheduleCurrentSequenceUpdateRequestDto requestDto = createSequenceUpdateRequestDto(NOT_EXIST_ID, DEFAULT_CURRENT_SEQUENCE);

            assertThatThrownBy(() -> scheduleService.updateCurrentSequence(userA.getId(), requestDto))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("다른 회원의 일정 ID를 전달하면 예외가 발생한다")
        void ifUseScheduleIdOfOtherUserThenThrowException() {
            ScheduleCurrentSequenceUpdateRequestDto requestDto = createSequenceUpdateRequestDto(scheduleB.getId(), DEFAULT_CURRENT_SEQUENCE);

            assertThatThrownBy(() -> scheduleService.updateCurrentSequence(userA.getId(), requestDto))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        private ScheduleCurrentSequenceUpdateRequestDto createSequenceUpdateRequestDto(long scheduleId, int sequence) {
            return ScheduleCurrentSequenceUpdateRequestDto.builder()
                    .scheduleId(scheduleId)
                    .sequence(sequence)
                    .build();
        }

        private int getSpaceAmount(Schedule schedule) {
            return schedule.getCourse()
                    .getSpaces()
                    .size();
        }

    }

    @Nested
    class createShareSerial {

        @Test
        @DisplayName("해당 일정에 대한 공유 시리얼을 생성하고, 그 값을 반환한다")
        void createsShareSerialAndReturn() {
            ShareSerialDto shareSerialDto = scheduleService.createShareSerial(userA.getId(), scheduleA.getId());

            assertThat(scheduleA.getShareSerial()).isNotNull();
            assertThat(scheduleA.getShareSerial()).isEqualTo(shareSerialDto.shareSerial());
        }

        @Test
        @DisplayName("공유 시리얼 생성 요청이 들어오면, 기존 공유 시리얼을 덮어써 새로운 값으로 대체한다")
        void overwriteExistShareSerial() {
            scheduleService.createShareSerial(userA.getId(), scheduleA.getId());
            String existShareSerial = scheduleA.getShareSerial();

            ShareSerialDto shareSerialDto = scheduleService.createShareSerial(userA.getId(), scheduleA.getId());
            String currentShareSerial = scheduleA.getShareSerial();

            assertThat(currentShareSerial).isNotEqualTo(existShareSerial);
            assertThat(currentShareSerial).isEqualTo(shareSerialDto.shareSerial());
        }

        @Test
        @DisplayName("존재하지 않는 회원의 ID를 전달하면 예외가 발생한다")
        void ifUseNotExistUserIdThenThrowException() {
            assertThatThrownBy(() -> scheduleService.createShareSerial(NOT_EXIST_ID, scheduleA.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 일정의 ID를 전달하면 예외가 발생한다")
        void ifUseNotExistScheduleIdThenThrowException() {
            assertThatThrownBy(() -> scheduleService.createShareSerial(userA.getId(), NOT_EXIST_ID))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("해당 회원의 일정이 아닌 일정의 ID를 전달하면 예외가 발생한다")
        void ifUseScheduleIdOfOtherUserThenThrowException() {
            assertThatThrownBy(() -> scheduleService.createShareSerial(userA.getId(), scheduleB.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

    }

    @Nested
    class findByShareSerial {

        @Test
        @DisplayName("공유 시리얼을 통해 조회한 일정 정보를 반환한다")
        void findScheduleByShareSerial() {
            scheduleA.setShareSerial(DEFAULT_SHARE_SERIAL);

            ScheduleResponseDto scheduleResponseDto = scheduleService.findByShareSerial(scheduleA.getShareSerial());

            assertThat(equalityChecker.check(scheduleA, scheduleResponseDto)).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 공유 시리얼을 전달하면 예외가 발생한다")
        void ifUseNotExistShareSerialThenThrowException() {
            assertThatThrownBy(() -> scheduleService.findByShareSerial(NOT_EXIST_SHARE_SERIAL))
                    .isInstanceOf(EntityNotFoundException.class);
        }

    }

    private void saveEntities(User user, List<SpaceDetail> spaceDetails) {
        spaceDetailRepository.saveAll(spaceDetails);
        userRepository.save(user);
    }

    private void registerUpcomingSchedule() {
        scheduleService.registerSchedule(vanillaUser.getId(), upcomingScheduleRequestDto);
    }

    private void registerPastSchedule() {
        scheduleService.registerSchedule(vanillaUser.getId(), pastScheduleRequestDto);
    }

}
