package success.planfit.comment.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.entity.comment.Comment;
import success.planfit.entity.like.CommentLike;
import success.planfit.entity.post.Post;
import success.planfit.entity.user.User;
import success.planfit.repository.CommentLikeRepository;
import success.planfit.repository.PostRepository;
import success.planfit.repository.UserRepository;

@Service
@Transactional
@AllArgsConstructor
public class CommentLikeService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;

    public void likeComment(Long userId, Long postId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않음"));

        Post post = postRepository.findByIdWithComment(postId)
                .orElseThrow(() -> new EntityNotFoundException("포스트가 존재하지 않음"));

        Comment comment = post.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("포스트가 존재하지 않음"));

        CommentLike commentLike = CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();

        comment.increaseLikeCount();
        commentLikeRepository.save(commentLike);
    }

    public void unlikeComment(Long userId, Long postId, Long commentId) {
        CommentLike commentLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId).stream()
                .filter(commentLikeForFilter -> commentLikeForFilter.getComment().getPost().getId().equals(postId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("포스트가 존재하지 않음"));

        commentLike.getComment().decreaseLikeCount();
        commentLikeRepository.delete(commentLike);
    }

}
