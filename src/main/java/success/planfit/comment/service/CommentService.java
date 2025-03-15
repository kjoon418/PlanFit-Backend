package success.planfit.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.comment.dto.CommentSaveRequestDto;
import success.planfit.entity.comment.Comment;
import success.planfit.entity.post.Post;
import success.planfit.entity.user.User;
import success.planfit.global.exception.EntityNotFoundException;
import success.planfit.repository.CommentRepository;
import success.planfit.repository.PostRepository;
import success.planfit.repository.UserRepository;



@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void registerComment(Long userId, Long postId, CommentSaveRequestDto requestDto){
        // 유저 조회
        User user = userRepository.findByIdWithComment(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));

        // Post 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("포스트 조회 실패"));

        // Comment 생성
        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .build();

        user.addComment(comment);
        post.addComment(comment);
    }

    public void removeComment(Long userId, Long postId, Long commentId){
        // 유저 조회
        User user = userRepository.findByIdWithComment(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));

        // Post 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("포스트 조회 실패"));

        // Comment 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("포스트 조회 실패"));

        // 삭제하려는 회원이 댓글 작성자인 경우만 삭제가능
        if (userId == comment.getUser().getId()) {
            user.removeComment(comment);
            post.removeComment(comment);
        }
    }
}
