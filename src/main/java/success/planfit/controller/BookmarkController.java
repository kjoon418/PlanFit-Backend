package success.planfit.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.controller.utils.ControllerUtil;
import success.planfit.dto.request.CourseBookmarkRegistrationRequestDto;
import success.planfit.dto.request.CourseBookmarkUpdateSpaceRequestDto;
import success.planfit.dto.request.CourseBookmarkUpdateTitleRequestDto;
import success.planfit.dto.request.SpaceBookmarkRegistrationRequestDto;
import success.planfit.dto.response.CourseBookmarkInfoResponseDto;
import success.planfit.dto.response.SpaceBookmarkInfoResponseDto;
import success.planfit.service.BookmarkService;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final ControllerUtil util;

    /**
     * 장소 좋아요 등록
     */
    @PostMapping("/space/bookmark")
    public ResponseEntity<Void> registerSpace(Principal principal, @RequestBody SpaceBookmarkRegistrationRequestDto requestDto) {
        log.info("BookmarkController.registerSpace() called");

        Long userId = util.findUserIdByPrincipal(principal);
        bookmarkService.registerSpaceBookmark(userId, requestDto);

        return ResponseEntity.ok().build();
    }

    /**
     * 장소 좋아요 삭제
     */
    @DeleteMapping("/space/bookmark/{identifier}")
    public ResponseEntity<Void> deleteSpace(Principal principal, @PathVariable("identifier") String googlePlacesIdentifier) {
        log.info("BookmarkController.deleteSpace() called");

        Long userId = util.findUserIdByPrincipal(principal);
        bookmarkService.deleteSpaceBookmark(userId, googlePlacesIdentifier);

        return ResponseEntity.ok().build();
    }

    /**
     * 장소 좋아요 조회
     */
    @GetMapping("/space/bookmark")
    public ResponseEntity<List<SpaceBookmarkInfoResponseDto>> findAllSpaceBookmarks(Principal principal) {
        log.info("BookmarkController.findAllSpaceBookmarks() called");

        Long userId = util.findUserIdByPrincipal(principal);
        List<SpaceBookmarkInfoResponseDto> responseDto = bookmarkService.findAllSpaceBookmarks(userId);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 코스 좋아요 등록
     */
    @PostMapping("/course/bookmark")
    public ResponseEntity<Void> registerCourse(Principal principal, @RequestBody CourseBookmarkRegistrationRequestDto requestDto) {
        log.info("BookmarkController.registerCourse() called");

        Long userId = util.findUserIdByPrincipal(principal);
        bookmarkService.registerCourseBookmark(userId, requestDto);

        return ResponseEntity.ok().build();
    }

    /**
     * 코스 좋아요 수정(대표 정보)
     */
    @PatchMapping("/course/bookmark/{courseId}")
    public ResponseEntity<Void> updateCourseTitleInfo(
            Principal principal,
            @PathVariable("courseId") Long courseBookmarkId,
            @RequestBody CourseBookmarkUpdateTitleRequestDto requestDto) {
        log.info("BookmarkController.updateCourseTitleInfo() called");

        Long userId = util.findUserIdByPrincipal(principal);
        bookmarkService.updateCourseBookmarkTitleInfo(userId, courseBookmarkId, requestDto);

        return ResponseEntity.ok().build();
    }

    /**
     * 코스 좋아요 수정(장소 정보)
     */
    @PatchMapping("/course/bookmark/{courseId}/{spaceId}")
    public ResponseEntity<Void> updateCourseSpaceInfo(
            Principal principal,
            @PathVariable("courseId") Long courseBookmarkId,
            @PathVariable("spaceId") Long timetableBookmarkId,
            @RequestBody CourseBookmarkUpdateSpaceRequestDto requestDto) {
        log.info("BookmarkController.updateCourseSpaceInfo() called");

        Long userId = util.findUserIdByPrincipal(principal);
        bookmarkService.updateCourseBookmarkSpaceInfo(userId, courseBookmarkId, timetableBookmarkId, requestDto);

        return ResponseEntity.ok().build();
    }

    /**
     * 코스 좋아요 전체 조회
     */
    @GetMapping("/course/bookmark")
    public ResponseEntity<List<CourseBookmarkInfoResponseDto>> findAllCourseBookmarks(Principal principal) {
        log.info("BookmarkController.findAllCourseBookmarks() called");

        Long userId = util.findUserIdByPrincipal(principal);
        List<CourseBookmarkInfoResponseDto> responseDto = bookmarkService.findAllCourseBookmarks(userId);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 코스 좋아요 단건 조회
     */
    @GetMapping("/course/bookmark/{courseId}")
    public ResponseEntity<CourseBookmarkInfoResponseDto> findCourseBookmark(Principal principal, @PathVariable("courseId") Long courseBookmarkId) {
        log.info("BookmarkController.findCourseBookmark() called");

        Long userId = util.findUserIdByPrincipal(principal);
        CourseBookmarkInfoResponseDto responseDto = bookmarkService.findCourseBookmark(userId, courseBookmarkId);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 코스 좋아요 삭제
     */
    @DeleteMapping("/course/bookmark/{courseId}")
    public ResponseEntity<Void> deleteCourseBookmark(Principal principal, @PathVariable("courseId") Long courseBookmarkId) {
        log.info("BookmarkController.deleteCourseBookmark() called");

        Long userId = util.findUserIdByPrincipal(principal);
        bookmarkService.deleteCourseBookmark(userId, courseBookmarkId);

        return ResponseEntity.ok().build();
    }
}
