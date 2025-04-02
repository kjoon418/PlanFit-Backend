package success.planfit.post.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.course.dto.CourseResponseDto;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.global.controller.PlanfitExceptionHandler;
import success.planfit.post.service.PostService;

@Slf4j
@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/post")
public class PostController {

    private final ControllerUtil controllerUtil;
    private final PlanfitExceptionHandler exceptionHandler;
    private final PostService postService;

    /**
     * 포스트 속 코스 정보 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<CourseResponseDto> findCourseInPublicPost(@PathVariable Long postId) {
        log.info("PostController.findCourseInPost()");

        CourseResponseDto responseDto = postService.findCourseInPublicPost(postId);

        return ResponseEntity.ok(responseDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("PostController.handleException() called");

        return exceptionHandler.handle(exception);
    }

}
