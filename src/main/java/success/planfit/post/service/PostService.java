package success.planfit.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.entity.course.Course;
import success.planfit.entity.post.Post;
import success.planfit.entity.schedule.Schedule;
import success.planfit.entity.user.User;
import success.planfit.post.dto.request.PostSaveRequestDtoByUser;
import success.planfit.repository.ScheduleRepository;
import success.planfit.course.dto.CourseResponseDto;
import success.planfit.entity.post.Post;
import success.planfit.global.exception.EntityNotFoundException;
import success.planfit.global.exception.IllegalRequestException;
import success.planfit.repository.PostRepository;
import success.planfit.repository.UserRepository;

import java.util.function.Supplier;
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class PostService {

    private static final Supplier<EntityNotFoundException> POST_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("해당 ID를 지닌 포스트를 찾을 수 없습니다.");

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    // 사용자가 코스 생성해서 포스팅
    public void registerPostByRegisteration(Long userId, PostSaveRequestDtoByUser requestDto){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));



    }

    // 사용자의 스케줄에서 불러와서 포스팅
    public void registerPostByPostId(Long userId, Long scheduleId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new RuntimeException("스케줄 조회 실패"));

    }

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
