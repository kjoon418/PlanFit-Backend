package success.planfit.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.domain.course.Calendar;
import success.planfit.domain.course.Timetable;
import success.planfit.domain.post.CoursePost;
import success.planfit.domain.post.SpacePost;
import success.planfit.domain.user.User;
import success.planfit.dto.request.CoursePostsSaveByCalendarRequestDto;
import success.planfit.dto.request.CoursePostsSaveRequestDto;
import success.planfit.dto.request.TimetableCreationRequestDto;
import success.planfit.dto.response.CoursePostInfoReponseDto;
import success.planfit.exception.EntityNotFoundException;
import success.planfit.photo.PhotoProvider;
import success.planfit.repository.CalendarRepository;
import success.planfit.repository.CoursePostRepository;
import success.planfit.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor (access = AccessLevel.PROTECTED)
@Service
public class PostService {

    private final UserRepository userRepository;
    private final CalendarRepository calendarRepository;
    private final CoursePostRepository coursePostRepository;

    // 코스를 생성해서 포스팅하는 경우
    public void addCoursePostByRegisteration(Long userId, CoursePostsSaveRequestDto requestDto) {
        // 유저 조회
        User user = userRepository.findByIdWithCoursePost(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));

        // 장소 timetable 뽑아서 SpacePost 만들기
        List<SpacePost> spacePosts = requestDto.getTimetable().stream()
                .map(TimetableCreationRequestDto::toEntity)
                .map(SpacePost::from).toList();

        // CoursePost 생성
        CoursePost coursePost = CoursePost.builder()
                .title(requestDto.getTitle())
                .location(requestDto.getLocation())
                .titlePhoto(PhotoProvider.decode(requestDto.getTitle()))
                .build();


        // 연관관계 편의 메소드로 연결 CoursePost - SpacePost
        for (SpacePost spacePost : spacePosts){
            coursePost.addSpacePost(spacePost);
        }

        user.addCoursePost(coursePost);
    }



    // 캘린더에 있는 코스를 불러와서 포스팅하는 경우
    public void addCoursePostByCalendar(Long userId, Long calendarId, CoursePostsSaveByCalendarRequestDto requestDto) {
        // 유저 조회
        User user = userRepository.findByIdWithCoursePost(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));

        // 저장된 코스에서 불러오기
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new IllegalArgumentException("코스 조회 실패"));

        // 일정에 있는 timetable 뽑아서 SpacePost 생성
        List<SpacePost> spacePosts = calendar.getTimetables().stream().map(SpacePost::from).toList();

        // CoursePost 생성
        CoursePost coursePost = CoursePost.builder()
                .title(requestDto.getTitle())
                .location(requestDto.getLocation())
                .titlePhoto(requestDto.getTitlePhoto())
                .build();

        // TODO: location 상의 필요

        // 연관관계 편의 메소드로 연결 CoursePost - SpacePost
        for (SpacePost spacePost : spacePosts){
            coursePost.addSpacePost(spacePost);
        }

        // user - coursePost 연결
        user.addCoursePost(coursePost);
    }


    // 코스 포스트 삭제
    public void deleteCoursePost(Long userId, Long postId) {

        User user = userRepository.findByIdWithCoursePost(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저조회 실패"));

        // TODO: 추가적인 예외처리 정리 필요

        CoursePost coursePost = coursePostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("코스 조회 실패"));

        user.removeCoursePost(coursePost);
    }

    // 자신의 포스트 모두 조회, 생성한 순서대로(최신순)
    public List<CoursePostInfoReponseDto> findAllCoursePosts(Long userId) {
        User user = userRepository.findByIdWithCoursePost(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저조회 실패"));


        List<CoursePost> coursePostList = coursePostRepository.findbyUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new IllegalArgumentException("포스트 없음"));

        // 정렬해서 spacePostDTO에 저장해줄것임
//        for ( CoursePost coursePost : coursePostList) {
////            coursePost.getSpacePosts().stream()
////                   .
//        }



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

    // 단건 포스트 조회
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
//        coursePosts.addUsers(userOwn);
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
//        coursePosts.removeUsers(userOwn);
    }


    // 코스 포스트 댓글 등록
    public void addCoursePostComments(){

    }


}
