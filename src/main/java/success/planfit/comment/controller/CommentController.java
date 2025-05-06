package success.planfit.comment.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.comment.dto.CommentSaveRequestDto;
import success.planfit.comment.service.CommentLikeService;
import success.planfit.comment.service.CommentService;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.global.controller.PlanfitExceptionHandler;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/post")
public class CommentController {

    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final ControllerUtil controllerUtil;
    private final PlanfitExceptionHandler exceptionHandler;

    @PostMapping("/{postId}/createComment")
    public ResponseEntity<Void> addComment(@PathVariable long postId,
                                           @RequestBody CommentSaveRequestDto requestDto,
                                           Principal principal){
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        commentService.registerComment(userId, postId, requestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/{commentId}/remove")
    public ResponseEntity<String> removeComment(
            @PathVariable Long postId
            ,@PathVariable Long commentId
            ,Principal principal) {
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        commentService.removeComment(userId, postId, commentId);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PostMapping("/{postId}/{commentId}")
    public ResponseEntity<Void> likeComment(@PathVariable long postId,
                                            @PathVariable long commentId, Principal principal){
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        commentLikeService.likeComment(userId, postId, commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/{commentId}")
    public ResponseEntity<Void> unlikeComment(@PathVariable long postId,
                                              @PathVariable long commentId, Principal principal){
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        commentLikeService.unlikeComment(userId, postId, commentId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("CommentController.handleException() called");

        return exceptionHandler.handle(exception);
    }

}
