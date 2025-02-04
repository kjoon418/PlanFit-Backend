package success.planfit.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import success.planfit.domain.bookmark.CourseBookmark;
import success.planfit.domain.bookmark.SpaceBookmark;
import success.planfit.domain.bookmark.TimetableBookmark;
import success.planfit.domain.course.Calendar;
import success.planfit.domain.user.User;
import success.planfit.dto.request.CourseBookmarkUpdateSpaceRequestDto;
import success.planfit.dto.request.CourseBookmarkUpdateTitleRequestDto;
import success.planfit.dto.request.SpaceBookmarkRegistrationRequestDto;
import success.planfit.dto.response.CourseBookmarkInfoResponseDto;
import success.planfit.dto.response.SpaceBookmarkInfoResponseDto;
import success.planfit.exception.EntityNotFoundException;
import success.planfit.exception.IllegalRequestException;
import success.planfit.photo.PhotoProvider;
import success.planfit.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkService {

    private final UserRepository userRepository;

    public void registerSpaceBookmark(Long userId, SpaceBookmarkRegistrationRequestDto requestDto) {
        log.info("BookmarkService.registerSpaceBookmark() called");

        // 엔티티 조회 및 생성
        User user = userRepository.findByIdWithSpaceBookmark(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));
        SpaceBookmark spaceBookmark = requestDto.toEntity();

        // 연관관계 편의 메서드 사용
        user.addSpaceBookmark(spaceBookmark);
    }

    public void deleteSpaceBookmark(Long userId, String googlePlacesIdentifier) {
        log.info("BookmarkService.deleteSpaceBookmark() called");

        // 엔티티 조회
        User user = userRepository.findByIdWithSpaceBookmark(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));
        SpaceBookmark spaceBookmark = user.getSpaceBookmarks().stream()
                .filter(spacebookmark -> spacebookmark.getGooglePlacesIdentifier().equals(googlePlacesIdentifier))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("좋아요에 등록된 장소 조회 실패"));

        // 연관관계 편의 메서드 사용
        user.removeSpaceBookmark(spaceBookmark);
    }

    @Transactional(readOnly = true)
    public List<SpaceBookmarkInfoResponseDto> findAllSpaceBookmarks(Long userId) {
        log.info("BookmarkService.findAllSpaceBookmarks() called");

        // 엔티티 조회
        User user = userRepository.findByIdWithSpaceBookmark(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 조회 실패"));

        // DTO로 변환하여 반환
        return user.getSpaceBookmarks().stream()
                .map(SpaceBookmarkInfoResponseDto::of)
                .toList();
    }

    public void registerCourseBookmark(Long userId, LocalDate date) {
        log.info("BookmarkService.registerCourseBookmark() called");

        // 엔티티 조회
        User user = userRepository.findByIdWithCalendar(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));

        // User를 통해 Date에 맞는 Calendar 조회
        Calendar calendar = user.getCalendars().stream()
                .filter(streamCalendar -> streamCalendar.getDate().equals(date))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("해당 날짜에 코스가 존재하지 않음"));

        // 해당 Calendar의 연관 Timetable 엔티티를 통해 TimetableBookmark 엔티티 생성
        List<TimetableBookmark> timetableBookmarks = calendar.getTimetables().stream()
                .map(TimetableBookmark::from)
                .toList();

        // 새로운 CourseBookmark 엔티티를 생성
        CourseBookmark courseBookmark = CourseBookmark.builder()
                .title(date.toString()) // 코스 이름의 기본값은 날짜로 함
                .titlePhoto(timetableBookmarks.getFirst().getSpaceInformation().getSpacePhoto()) // 코스 사진의 기본값은 첫 번째 장소로 함
                .build();

        // CourseBookmark 엔티티와 TimetableBookmark 엔티티 연결
        for (TimetableBookmark timetableBookmark : timetableBookmarks) {
            courseBookmark.addTimetableBookmark(timetableBookmark);
        }

        // User 엔티티와 CourseBookmark 엔티티 연결
        user.addCourseBookmark(courseBookmark);
    }

    public void updateCourseBookmarkTitleInfo(Long userId, Long courseBookmarkId, CourseBookmarkUpdateTitleRequestDto requestDto) {
        log.info("BookmarkService.updateCourseBookmarkTitleInfo() called");

        // 엔티티 조회
        User user = userRepository.findByIdWithCourseBookmark(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));
        CourseBookmark courseBookmark = user.getCourseBookmarks().stream()
                .filter(coursebookmark -> coursebookmark.getId().equals(courseBookmarkId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("코스 좋아요 조회 실패"));

        // 엔티티 수정
        updateIfNotNull(courseBookmark::setTitle, requestDto.getTitle());
        switch (requestDto.getPhotoType()) {
            case URL -> updateIfNotNull(courseBookmark::setTitlePhoto, PhotoProvider.getImageFromUrl(requestDto.getTitlePhoto()));
            case ENCODED_BINARY -> updateIfNotNull(courseBookmark::setTitlePhoto, PhotoProvider.decode(requestDto.getTitlePhoto()));
        }
    }

    public void updateCourseBookmarkSpaceInfo(Long userId, Long courseBookmarkId, Long timetableBookmarkId, CourseBookmarkUpdateSpaceRequestDto requestDto) {
        log.info("BookmarkService.updateCourseBookmarkSpaceInfo() called");

        // 엔티티 조회
        User user = userRepository.findByIdWithCourseBookmark(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));
        CourseBookmark courseBookmark = user.getCourseBookmarks().stream()
                .filter(coursebookmark -> coursebookmark.getId().equals(courseBookmarkId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("코스 좋아요 조회 실패"));
        TimetableBookmark timetableBookmark = courseBookmark.getTimetableBookmarks().stream()
                .filter(timetablebookmark -> timetablebookmark.getId().equals(timetableBookmarkId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("장소 조회 실패"));

        // 엔티티 수정
        updateIfNotNull(timetableBookmark::setMemo, requestDto.getMemo());
        timetableBookmark.setSpaceInformation(timetableBookmark
                .getSpaceInformation() // 기존의 SpaceInformation
                .copyNotNulls(requestDto.getSpaceInformation())); // RequestDto의 SpaceInformation 정보 반영
    }

    public void updateCourseBookmarkSpaceSequence(Long userId, Long courseBookmarkId, List<Long> spaceIds) {
        log.info("BookmarkService.updateCourseBookmarkSpaceSequence() called");

        // 엔티티 조회
        User user = userRepository.findByIdWithCourseBookmark(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));
        CourseBookmark courseBookmark = user.getCourseBookmarks().stream()
                .filter(coursebookmark -> coursebookmark.getId().equals(courseBookmarkId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("코스 좋아요 조회 실패"));

        // 로직에 사용하기 위해 List<TimetableBookmark>를 Map<Long, TimetableBookmark>로 매핑
        HashMap<Long, TimetableBookmark> timetableBookmarks = new HashMap<>();
        for (TimetableBookmark timetableBookmark : courseBookmark.getTimetableBookmarks()) {
            timetableBookmarks.put(timetableBookmark.getId(), timetableBookmark);
        }

        // 유효성 검사
        if (isIllegalSequenceUpdateRequest(timetableBookmarks, spaceIds)) {
            throw new IllegalRequestException("장소 ID 부적절");
        }

        // 요청의 순서대로 엔티티의 sequence 값 변경
        int sequence = 1;
        for (Long spaceId : spaceIds) {
            TimetableBookmark timetableBookmark = timetableBookmarks.get(spaceId);
            timetableBookmark.setSequence(sequence);
            sequence++;
        }
    }

    @Transactional(readOnly = true)
    public List<CourseBookmarkInfoResponseDto> findAllCourseBookmarks(Long userId) {
        log.info("BookmarkService.findAllCourseBookmarks() called");

        // 엔티티 조회
        User user = userRepository.findByIdWithCourseBookmark(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 조회 실패"));

        // Dto로 변환하여 반환
        return user.getCourseBookmarks().stream()
                .map(CourseBookmarkInfoResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseBookmarkInfoResponseDto findCourseBookmark(Long userId, Long courseBookmarkId) {
        log.info("BookmarkService.findCourseBookmark() called");

        // 엔티티 조회
        User user = userRepository.findByIdWithCourseBookmark(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 조회 실패"));
        CourseBookmark courseBookmark = user.getCourseBookmarks().stream()
                .filter(coursebookmark -> coursebookmark.getId().equals(courseBookmarkId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("코스 조회 실패"));

        // DTO로 변환하여 반환
        return CourseBookmarkInfoResponseDto.from(courseBookmark);
    }

    public void deleteCourseBookmark(Long userId, Long courseBookmarkId) {
        log.info("BookmarkService.deleteCourseBookmark() called");

        // 엔티티 조회
        User user = userRepository.findByIdWithCourseBookmark(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 조회 실패"));
        CourseBookmark courseBookmark = user.getCourseBookmarks().stream()
                .filter(coursebookmark -> coursebookmark.getId().equals(courseBookmarkId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("코스 조회 실패"));

        // 엔티티 삭제
        user.removeCourseBookmark(courseBookmark);
    }

    private <T> void updateIfNotNull(Consumer<T> setter, T value) {
        if (value == null) {
            return;
        }

        // 문자열이 아닐 경우 null이 아니기만 하면 통과, 문자열일 경우 StringUtils.hasText()가 true여야 통과
        if (!(value instanceof String) || StringUtils.hasText((String) value)) {
            setter.accept(value);
        }
    }

    /**
     * 순서 변경 요청이 부적합한지를 검사하고 여부를 반환하는 메서드
     * true - 부적합
     */
    private boolean isIllegalSequenceUpdateRequest(Map<Long, TimetableBookmark> timetableBookmarks, List<Long> spaceIds) {
        // 유효성 검사 1. 장소의 개수가 일치하는지 확인
        if (spaceIds.size() != timetableBookmarks.size()) {
            return true;
        }

        Set<Long> idDuplicateCheckSet = new HashSet<>();
        for (Long spaceId : spaceIds) {
            // 유효성 검사 2. 중복되는 id가 있는지 확인
            if (idDuplicateCheckSet.contains(spaceId)) {
                return true;
            }
            idDuplicateCheckSet.add(spaceId);

            // 유효성 검사 3. 기존 엔티티에 없는 id가 있는지 확인
            if (!timetableBookmarks.containsKey(spaceId)) {
                return true;
            }
        }

        return false;
    }
}
