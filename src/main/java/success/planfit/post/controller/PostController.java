package success.planfit.post.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.course.dto.CourseResponseDto;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.global.controller.PlanfitExceptionHandler;
import success.planfit.post.dto.request.PostRequestDto;
import success.planfit.post.dto.response.PostInfoDto;
import success.planfit.post.service.PostService;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final ControllerUtil controllerUtil;
    private final PlanfitExceptionHandler exceptionHandler;
    private final PostService postService;

    /**
     * 포스팅
     */
    @PostMapping
    public ResponseEntity<Void> registerPost(Principal principal,
                                             PostRequestDto requestDto) {
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        postService.registerPost(userId, requestDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 포스트 속 코스 정보 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<CourseResponseDto> findCourseInPublicPost(@PathVariable long postId) {
        log.info("PostController.findCourseInPost()");

        CourseResponseDto responseDto = postService.findCourseInPublicPost(postId);

        return ResponseEntity.ok(responseDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("PostController.handleException() called");

        return exceptionHandler.handle(exception);
    }

    /**
     * 포스트 단건 조회
     */
    @GetMapping("/{postId}/get")
    public ResponseEntity<PostInfoDto> findPostById(@PathVariable long postId){
        PostInfoDto post = postService.findPost(postId);
        return ResponseEntity.ok(post);
    }

    /**
     * 포스트 최신순 3건 조회
     */
    @GetMapping("/get/{postNum}")
    public ResponseEntity<List<PostInfoDto>> findRecentPosts(int postNum){
        List<PostInfoDto> postInfoDtoList = postService.findRecentPosts(postNum);
        return ResponseEntity.ok(postInfoDtoList);
    }

    /**
     * 포스트 최신순 전체 조회
     */
    @GetMapping
    public ResponseEntity<List<PostInfoDto>> findAllOrderByCreatedAt(
            @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
            @RequestParam(required = false, defaultValue = "createdAt", value = "criteria") String criteria
    ){
        List<PostInfoDto> postInfoDtos = postService.findAll(pageNo, criteria);
        return ResponseEntity.ok(postInfoDtos);
    }

    /**
     * 포스트 수정
     */
    @PatchMapping("/{postId}")
    public ResponseEntity<Void> updatePost(Principal principal, long postId, PostRequestDto requestDto){
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        postService.updatePost(userId, postId, requestDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 포스트 삭제
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(Principal principal, @PathVariable long postId) {
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        postService.deletePost(userId, postId);
        return ResponseEntity.ok().build();
    }
}
