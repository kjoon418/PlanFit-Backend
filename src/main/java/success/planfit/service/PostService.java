package success.planfit.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.domain.course.Calendar;
import success.planfit.domain.post.CoursePost;
import success.planfit.domain.post.SpacePost;
import success.planfit.domain.user.User;
import success.planfit.dto.request.CoursePostsSaveRequestDto;
import success.planfit.dto.response.CoursePostInfoReponseDto;
import success.planfit.exception.EntityNotFoundException;
import success.planfit.repository.SpacePostRepository;
import success.planfit.repository.UserRepository;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor (access = AccessLevel.PROTECTED)
@Service
public class PostService {

    private final UserRepository userRepository;
    private final SpacePostRepository savePostRepository;

    public void addCoursePost(Long userId, CoursePostsSaveRequestDto requestDto) {
        User user = userRepository.findByIdWithCoursePost(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));

        Calendar calendar = user.getCalendars().stream()
                .filter(streamCalendar -> streamCalendar.getDate().equals(requestDto.getDate()))
                .findAny()
                .orElseThrow(()-> new IllegalArgumentException("해당 날짜에 코스가 존재하지 않음"));

        List<SpacePost> spacePosts = calendar.getTimetables().stream().map(SpacePost::from).toList();

        CoursePost coursePost = CoursePost.builder()
                .user(user)
                .title(requestDto.getTitle())
                .location(spacePosts.getFirst().getSpaceInformation().getLocation())
                .titlePhoto(spacePosts.getFirst().getSpaceInformation().getSpacePhoto())
                .build();

        for (SpacePost spacePost : spacePosts){
            coursePost.addSpacePost(spacePost);
        }

        user.addCoursePost(coursePost);
    }



    public void deleteCoursePost(Long userId, Long postId) {
        User user = userRepository.findByIdWithCoursePost(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저조회 실패"));

        CoursePost coursePost = user.getCoursePosts().stream()
                .filter(post-> post.getId().equals(postId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("코스 조회 실패"));

        user.removeCoursePost(coursePost);
    }


    public List<CoursePostInfoReponseDto> findAllCoursePosts(Long userId) {
        User user = userRepository.findByIdWithCoursePost(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저조회 실패"));

        return user.getCoursePosts().stream()
                .map(CoursePostInfoReponseDto::from)
                .toList();
    }

    // 다른 사람의 코스 포스트 목록 조회 (User 자신 포함)
    public List<CoursePostInfoReponseDto> findAllUsersCoursePosts(){
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers){
            return user.getCoursePosts().stream()
                    .map(CoursePostInfoReponseDto::from)
                    .toList();
        }
        return null;
    }

    public CoursePostInfoReponseDto findByCoursePostId(Long userId, Long coursePostId){
        User user = userRepository.findByIdWithCoursePost(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저조회 실패"));

        return user.getCoursePosts().stream()
                .filter(coursePost -> coursePost.getId().equals(coursePostId))
                .map(CoursePostInfoReponseDto::from)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("포스트 상세 조회 실패"));
    }


    // 코스 포스트를 관심 코스로 등록
    public void saveCoursePostBookmark(Long userId, Long coursePostId) {
        User userOwn = userRepository.findByIdWithCoursePost(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저조회 실패"));
        CoursePost coursePosts = null;
        // 해당 포스트 찾기
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers){
            coursePosts = user.getCoursePosts().stream()
                    .filter(coursePost -> coursePost.getId().equals(coursePostId))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("해당 포스트 조회 실패"));
        }
        coursePosts.addUsers(userOwn);
    }

    // 코스 포스트를 관심 코스에서 제거 (CoursePostBookmark)
    public void delteCoursePostBookmark(Long userId, Long coursePostId) {
        User userOwn = userRepository.findByIdWithCoursePost(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저조회 실패"));
        CoursePost coursePosts = null;
        // 해당 포스트 찾기
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers){
            coursePosts = user.getCoursePosts().stream()
                    .filter(coursePost -> coursePost.getId().equals(coursePostId))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("해당 포스트 조회 실패"));
        }
        coursePosts.removeUsers(userOwn);
    }


    // 코스 포스트 댓글 등록
    public void addCoursePostComments(){

    }


}
