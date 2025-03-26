package success.planfit.post.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.course.dto.CourseResponseDto;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.post.dto.request.PostSaveRequestDtoByUser;
import success.planfit.post.dto.request.PostSaveRequestFromSchedule;
import success.planfit.post.dto.response.PostInfoDto;
import success.planfit.post.service.PostService;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/post")
public class PostController {

    private final ControllerUtil controllerUtil;
    private final PostService postService;

    // 사용자가 코스 생성해서 포스팅
    @PostMapping
    public ResponseEntity<Void> registerPostByUser(Principal principal,
                                                   PostSaveRequestDtoByUser requestDto) {
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        postService.registerPostByUser(userId, requestDto);
        return ResponseEntity.ok().build();
    }

    // 사용자의 스케줄에서 불러와서 포스팅
    @PostMapping
    public ResponseEntity<Void> registerPostBySchedule(Principal principal, PostSaveRequestFromSchedule requestDto){
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        postService.registerPostByPostId(userId, requestDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 포스트 속 코스 정보 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<CourseResponseDto> findCourseInPublicPost(@PathVariable Long postId) {
        log.info("PostController.findCourseInPost()");

        CourseResponseDto responseDto = postService.findCourseInPublicPost(postId);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 포스트 단건 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostInfoDto> findPostById(Principal principal, @PathVariable Long postId){
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        PostInfoDto post = postService.findPost(userId, postId);
        return ResponseEntity.ok(post);
    }




//    @PatchMapping
//    public ResponseEntity<Void> updatePost(Principal principal, PostUpdateDto requestDto){
//        Long userId = controllerUtil.findUserIdByPrincipal(principal);
//        postService.updatePost(userId, requestDto);
//        return ResponseEntity.ok().build();
//    }








    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(Principal principal, @PathVariable Long postId) {
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        postService.deletePost(userId, postId);
        return ResponseEntity.ok().build();
    }


}
