package success.planfit.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.course.dto.CourseResponseDto;
import success.planfit.entity.post.Post;
import success.planfit.global.exception.EntityNotFoundException;
import success.planfit.global.exception.IllegalRequestException;
import success.planfit.repository.PostRepository;
import success.planfit.repository.UserRepository;

import java.util.function.Supplier;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService {

    private static final Supplier<EntityNotFoundException> POST_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("해당 ID를 지닌 포스트를 찾을 수 없습니다.");

    private final PostRepository postRepository;
    private final UserRepository userRepository;

//    public Long addCoursePost(){
//
//    }

    @Transactional(readOnly = true)
    public CourseResponseDto findCourseInPublicPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(POST_NOT_FOUND_EXCEPTION);
        validatePublic(post);

        return CourseResponseDto.from(post.getCourse());
    }

    private void validatePublic(Post post) {
        if (!post.getIsPublic()) {
            throw new IllegalRequestException("비공개 포스트의 정보는 조회할 수 없습니다.");
        }
    }

}
