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
import success.planfit.repository.UserRepository;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class PostService {

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


}
