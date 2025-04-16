package success.planfit.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.comment.dto.CommentSaveRequestDto;
import success.planfit.entity.comment.Comment;
import success.planfit.entity.post.Post;
import success.planfit.entity.user.User;
import jakarta.persistence.EntityNotFoundException;
import success.planfit.repository.CommentLikeRepository;
import success.planfit.repository.PostRepository;
import success.planfit.repository.UserRepository;
import java.util.function.Supplier;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class CommentService {

    private static final Supplier<EntityNotFoundException> USER_NOT_FOUND_EXCEPTION = () -> new jakarta.persistence.EntityNotFoundException("유저 조회에 실패했습니다.");
    private static final Supplier<EntityNotFoundException> POST_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("해당 ID를 지닌 포스트를 찾을 수 없습니다.");

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;

    public void registerComment(Long userId, Long postId, CommentSaveRequestDto requestDto){
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(USER_NOT_FOUND_EXCEPTION);

        // Post 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(POST_NOT_FOUND_EXCEPTION);

        // Comment 생성
        Comment comment = Comment.builder()
                .user(user)
                .content(requestDto.getContent())
                .build();

        post.addComment(comment);
        postRepository.save(post);
    }

    public void removeComment(Long userId, Long postId, Long commentId){
        // Post 조회
        Post post = postRepository.findByIdWithComment(postId)
                .orElseThrow(POST_NOT_FOUND_EXCEPTION);

        // 댓글 가져오기
        Comment comment = post.getComments().stream()
                .filter(commentForFilter -> commentForFilter.getId().equals(commentId)  && commentForFilter.getUser().getId().equals(userId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("댓글 조회 실패"));

        // 해당 댓글의 댓글 좋아요 모두 삭제
        commentLikeRepository.deleteAllByCommentId(commentId);
        post.removeComment(comment);
    }
}
