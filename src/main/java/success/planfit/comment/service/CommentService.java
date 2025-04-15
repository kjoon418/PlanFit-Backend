package success.planfit.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.comment.dto.CommentSaveRequestDto;
import success.planfit.entity.comment.Comment;
import success.planfit.entity.post.Post;
import success.planfit.entity.user.User;
import success.planfit.global.exception.EntityNotFoundException;
import success.planfit.repository.CommentLikeRepository;
import success.planfit.repository.PostRepository;
import success.planfit.repository.UserRepository;

import java.util.List;


@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;

    public void registerComment(Long userId, Long postId, CommentSaveRequestDto requestDto){
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));

        // Post 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("포스트 조회 실패"));

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
                .orElseThrow(() -> new EntityNotFoundException("포스트 조회 실패"));

        // 댓글 가져오기
        Comment comment = post.getComments().stream()
                .filter(c -> c.getId().equals(commentId)  && c.getUser().getId().equals(userId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("댓글 조회 실패"));

        // 해당 댓글의 댓글 좋아요 모두 삭제
        commentLikeRepository.deleteAllByCommentId(commentId);
        post.removeComment(comment);
    }
}
