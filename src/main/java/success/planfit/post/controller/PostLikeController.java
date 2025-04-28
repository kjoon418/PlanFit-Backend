package success.planfit.post.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.global.controller.PlanfitExceptionHandler;
import success.planfit.post.dto.response.PostPreviewDto;
import success.planfit.post.service.PostLikeService;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final ControllerUtil controllerUtil;
    private final PlanfitExceptionHandler exceptionHandler;

    @PostMapping("/{postId}")
    public ResponseEntity<String> likePost(@PathVariable long postId, Principal principal) {
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        postLikeService.likePost(userId, postId);

        return ResponseEntity.ok("포스트를 좋아요 했습니다.");
    }

    @GetMapping
    public ResponseEntity<List<PostPreviewDto>> getLikedPosts(Principal principal) {
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        return ResponseEntity.ok(postLikeService.getLikedPosts(userId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> unlikePost(@PathVariable long postId, Principal principal) {
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        postLikeService.unlikePost(userId, postId);

        return ResponseEntity.ok("포스트 좋아요 취소했습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("PostLikeController.handleException() called");

        return exceptionHandler.handle(exception);
    }

}
