package success.planfit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.domain.bookmark.CoursePostBookmark;
import success.planfit.domain.course.Calendar;
import success.planfit.domain.course.Timetable;
import success.planfit.domain.embeddable.SpaceInformation;
import success.planfit.domain.post.CoursePost;
import success.planfit.domain.post.SpacePost;
import success.planfit.domain.user.User;
import success.planfit.dto.request.CalendarSaveRequestDto;
import success.planfit.dto.request.TimetableCreationRequestDto;
import success.planfit.dto.request.TimetableUpdateRequestDto;
import success.planfit.dto.response.TimetableInfoResponseDto;
import success.planfit.repository.CoursePostBookmarkRepository;
import success.planfit.repository.CoursePostRepository;
import success.planfit.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class TimetableService {

    private final UserRepository userRepository;
    private final CoursePostRepository coursePostRepository;
    private final CoursePostBookmarkRepository coursePostBookmarkRepository;

    public void addTimetable(Long userId, TimetableCreationRequestDto requestDto, LocalDate date ) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));
        Calendar calendar = user.getCalendars().stream().filter(cal -> cal.getDate().equals(date))
                .findFirst().orElseGet(() -> Calendar.builder().date(date).title(date.toString()).build());
        user.addCalendar(calendar);
        Timetable timetable = requestDto.toEntity();
        calendar.addTimetable(timetable);
    }

    // 코스 직접 생성
    public void addTimetable1(Long userId, CalendarSaveRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));
        // 코스 생성
        Calendar calendar = Calendar.builder()
                .date(requestDto.getDate())
                .title(requestDto.getTitle())
                .build();

        // timetable 뽑아서 calendar와 연결하기
        List<TimetableCreationRequestDto> timetableList = requestDto.getTimetable();
        for (TimetableCreationRequestDto request : timetableList) {
            calendar.addTimetable(request.toEntity());
        }

        // user와 calendar 연결
        user.addCalendar(calendar);
    }

    // 포스트에서 나의 일정에 추가 -> DTO를 따로 만들어야되나?(그래야될 것같기도함), 아 근데 불러오는거라서
    public void addTimetableFromPost(Long userId, Long postId, CalendarSaveRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));
        CoursePost coursePost = coursePostRepository.findById(postId).orElseThrow(() -> new RuntimeException("포스트 조회 실패"));

        // 코스 생성
        Calendar calendar = Calendar.builder()
                .date(requestDto.getDate())
                .title(requestDto.getTitle())
                .build();

        // spacePost -> timetable로 만들기
        for (SpacePost spacePost : coursePost.getSpacePosts()) {
            Timetable timetable = Timetable.builder()
                    .sequence(spacePost.getSequence())
                    .spaceInformation(spacePost.getSpaceInformation())
                    .build();

            // calendar와 timetable 연관관계 편의 메소드로 연결
            calendar.addTimetable(timetable);
        }

        // user에 calendar(코스) 연결
        user.addCalendar(calendar);
    }

    // 북마크한 포스트에서 내 일정에 추가
    public void addTimetableFromBookmark(Long userId, Long courPostBookmarkId, CalendarSaveRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));

        // CoursePostBookmark 가져오기
        CoursePostBookmark coursePostBookmark = coursePostBookmarkRepository.findById(courPostBookmarkId).orElseThrow(() -> new RuntimeException("즐겨찾기 포스트 조회 실패"));
        // CoursePost 가져오기 (CoursePostBookmark에서)
        CoursePost coursePost = coursePostBookmark.getCoursePost();

        // 코스 생성
        Calendar calendar = Calendar.builder()
                .date(requestDto.getDate())
                .title(requestDto.getTitle())
                .build();

        // spacePost -> timetable로 만들기
        for (SpacePost spacePost : coursePost.getSpacePosts()) {
            Timetable timetable = Timetable.builder()
                    .sequence(spacePost.getSequence())
                    .spaceInformation(spacePost.getSpaceInformation())
                    .build();

            // calendar와 timetable 연관관계 편의 메소드로 연결
            calendar.addTimetable(timetable);
        }

        // user에 calendar(코스) 연결
        user.addCalendar(calendar);
    }



    // 코스 삭제
    public void removeTimetable(Long userId, LocalDate date, Long timetableId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("유저 조회 실패"));
        Calendar calendar = user.getCalendars().stream().filter(cal -> cal.getDate().equals(date)).findFirst().orElseThrow(() -> new RuntimeException("캘린더 조회 실패"));
        Timetable timetable = calendar.getTimetables().stream().filter(timetb -> timetb.getId().equals(timetableId)).findFirst().orElseThrow(() -> new RuntimeException("타임 테이블 조회 실패"));
        calendar.removeTimetable(timetable);
    }

    public TimetableInfoResponseDto updateTimetable(Long userId, LocalDate date, Long timetableId, TimetableUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));
        Calendar calendar = user.getCalendars().stream()
                .filter(cal -> cal.getDate().equals(date))
                .findAny()
                .orElseThrow(() -> new RuntimeException("캘린더 조회 실패"));
        Timetable timetable = calendar.getTimetables().stream()
                .filter(timetb -> timetb.getId().equals(timetableId))
                .findAny()
                .orElseThrow(() -> new RuntimeException("타임 테이블 조회 실패"));

        timetable.setMemo(requestDto.getMemo() != null ? requestDto.getMemo() : timetable.getMemo());
        SpaceInformation existsSpaceInfo = timetable.getSpaceInformation();
        SpaceInformation newSpaceInfo = existsSpaceInfo.copyNotNulls(requestDto.getSpaceInformation());
        timetable.setSpaceInformation(newSpaceInfo);

        return TimetableInfoResponseDto.from(timetable);
    }

    public List<TimetableInfoResponseDto> getTimetables(Long userId, LocalDate date) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));

        return  user.getCalendars().stream()
                .filter(cal -> cal.getDate().equals(date))
                .findAny()
                .orElseThrow(() -> new RuntimeException("타임 테이블 조회 실패"))
                .getTimetables().stream()
                .map(TimetableInfoResponseDto::from)
                .toList();
    }

}
