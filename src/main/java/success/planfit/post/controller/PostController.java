package success.planfit.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(
        name = "포스트 API",
        description = "포스트 조회/생성/관리 관련 기능"
)
public class PostController {

    private final ControllerUtil controllerUtil;
    private final PlanfitExceptionHandler exceptionHandler;
    private final PostService postService;

    @PostMapping
    @Operation(
            summary = "포스트 등록",
            description = "정보를 기반으로 새로운 포스트를 등록합니다."
    )
    public ResponseEntity<Void> registerPost(Principal principal,
                                             @Valid @RequestBody PostRequestDto requestDto) {
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        postService.registerPost(userId, requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}")
    @Operation(
            summary = "포스트 속 코스 정보 조회",
            description = "포스트 속 코스 정보를 조회합니다."
    )
    public ResponseEntity<CourseResponseDto> findCourseInPublicPost(@PathVariable long postId) {
        log.info("PostController.findCourseInPost()");

        CourseResponseDto responseDto = postService.findCourseInPublicPost(postId);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{postId}/get")
    @Operation(
            summary = "포스트 단건 조회",
            description = "ID를 통해 포스트 하나를 조회합니다."
    )
    public ResponseEntity<PostInfoDto> findPostById(@PathVariable long postId){
        PostInfoDto post = postService.findPost(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/get/{postNum}")
    @Operation(
            summary = "포스트 최신순 N건 조회",
            description = "가장 최신 포스트를 postNum만큼 조회합니다."
    )
    public ResponseEntity<List<PostInfoDto>> findRecentPosts(@PathVariable int postNum){
        List<PostInfoDto> postInfoDtoList = postService.findRecentPosts(postNum);
        return ResponseEntity.ok(postInfoDtoList);
    }

    @GetMapping
    @Operation(
            summary = "포스트 조회(페이지네이션)",
            description = "정렬된 포스트를 페이지네이션 방식으로 조회합니다. 정렬 기준은 criteria가 결정합니다. 기준을 지정하지 않을 시, 최신순으로 정렬합니다."
    )
    public ResponseEntity<List<PostInfoDto>> findAllOrderByCreatedAt(
            @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
            @RequestParam(required = false, defaultValue = "createdAt", value = "criteria") String criteria
    ){
        List<PostInfoDto> postInfoDtos = postService.findAll(pageNo, criteria);
        return ResponseEntity.ok(postInfoDtos);
    }

    @PatchMapping("/{postId}")
    @Operation(
            summary = "포스트 수정",
            description = "포스트 정보를 새로운 정보로 수정합니다. 기존 포스트 정보를 모두 덮어씌웁니다."
    )
    public ResponseEntity<Void> updatePost(Principal principal, long postId,
                                           @Valid @RequestBody PostRequestDto requestDto){
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        postService.updatePost(userId, postId, requestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    @Operation(
            summary = "포스트 삭제",
            description = "ID로 조회한 포스트를 삭제합니다."
    )
    public ResponseEntity<Void> deletePost(Principal principal, @PathVariable long postId) {
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        postService.deletePost(userId, postId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("PostController.handleException() called");

        return exceptionHandler.handle(exception);
    }

}
