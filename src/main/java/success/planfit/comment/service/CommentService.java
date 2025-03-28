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
import success.planfit.repository.CommentRepository;
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

    public void registerComment(Long userId, Long postId, CommentSaveRequestDto requestDto){
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));

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

        // 유저의 댓글 가져오기
        Comment comment = findCommentsById(user, commentId);

        user.removeComment(comment);
        post.removeComment(comment);
    }

    private Comment findCommentsById(User user, Long commentId){
        return user.getComments().stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("포스트 조회 실패"));
    }




}
