package success.planfit.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.post.service.PostLikeService;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/{postId}")
    public ResponseEntity<String> likePost(@RequestParam Long userId, @PathVariable Long postId) {
        postLikeService.likePost(userId, postId);

        return ResponseEntity.ok("포스트를 좋아요 했습니다.");
    }

    @GetMapping
    public ResponseEntity<List<Long>> getLikedPosts(@RequestParam Long userId) {
        return ResponseEntity.ok(postLikeService.getLikedPosts(userId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> unlikePost(@RequestParam Long userId, @PathVariable Long postId) {
        postLikeService.unlikePost(userId, postId);

        return ResponseEntity.ok("포스트 좋아요 취소했습니다.");
    }

}
