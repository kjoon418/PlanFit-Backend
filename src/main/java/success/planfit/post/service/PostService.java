package success.planfit.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.course.dto.SpaceResponseDto;
import success.planfit.entity.course.Course;
import success.planfit.entity.post.Post;
import success.planfit.entity.post.PostPhoto;
import success.planfit.entity.schedule.Schedule;
import success.planfit.entity.space.Space;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.space.SpacePhoto;
import success.planfit.entity.user.User;
import success.planfit.global.photo.PhotoProvider;
import success.planfit.post.dto.request.PostSaveRequestDtoByUser;
import success.planfit.post.dto.request.PostSaveRequestFromSchedule;
import success.planfit.post.dto.request.PostUpdateDto;
import success.planfit.post.dto.response.PostInfoDto;
import success.planfit.course.dto.CourseResponseDto;
import success.planfit.global.exception.EntityNotFoundException;
import success.planfit.global.exception.IllegalRequestException;
import success.planfit.repository.PostRepository;
import success.planfit.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class PostService {
    private static final Supplier<EntityNotFoundException> POST_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("해당 ID를 지닌 포스트를 찾을 수 없습니다.");

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 사용자가 코스 생성해서 포스팅
    public void registerPostByUser(Long userId, PostSaveRequestDtoByUser requestDto) {
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));

        // 코스 생성
        Course course = Course.builder().location(requestDto.getLocation()).build();

        // 장소리스트로 돌기
        for (SpaceResponseDto spaceResponseDto : requestDto.getSpaceList()) {

            // 1. SpaceDetail에 저장 - 캐시저장
            SpaceDetail spaceDetail = SpaceDetail.builder()
                    .googlePlacesIdentifier(spaceResponseDto.getGooglePlacesIdentifier())
                    .spaceName(spaceResponseDto.getName())
                    .location(spaceResponseDto.getLocation())
                    .spaceType(spaceResponseDto.getSpaceType())
                    .link(spaceResponseDto.getLink())
                    .latitude(spaceResponseDto.getLatitude())
                    .longitude(spaceResponseDto.getLongitude())
                    .build();

            // 장소 사진 디코드
            List<byte[]> spacePhotoList = spaceResponseDto.getSpacePhotos().stream()
                    .map(PhotoProvider::decode)
                    .toList();

            // 디코드된 사진 리스트로 SpacePhoto 생성
            for (byte[] sp : spacePhotoList) {
                SpacePhoto spacePhoto = SpacePhoto.builder().value(sp).build();
                // SpaceDetail과 연결
                spaceDetail.addSpacePhoto(spacePhoto);
            }

            // 2. Space 생성
            Space space = Space.builder()
                    .spaceDetail(spaceDetail)
                    .build();
            // 3. Course와 Space 연결
            course.addSpace(space);
        }

        // dto -> 디코드한 postPhotoList
        List<byte[]> postPhotoList = requestDto.getPostPhotoList().stream()
                .map(PhotoProvider::decode)
                .toList();

        // post 생성
        Post post = Post.builder()
                .course(course)
                .content(requestDto.getContent())
                .title(requestDto.getTitle())
                .isPublic(requestDto.getIsPublic())
                .build();

        for (byte[] p : postPhotoList) {
            PostPhoto postPhoto = PostPhoto.builder()
                    .photo(p)
                    .build();
            // 포스트와 포스트 사진 리스트 연결
            post.addPostPhoto(postPhoto);
        }

        // user와 post 연결
        user.addPost(post);
    }

    // 사용자의 스케줄에서 불러와서 포스팅
    public void registerPostByPostId(Long userId, PostSaveRequestFromSchedule requestDto) {
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));
        // 스케줄 반환
        Schedule schedule = user.getSchedules().stream()
                .filter(sc -> sc.getId().equals(requestDto.getScheduleId()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("스케줄 조회 실패"));

        List<byte[]> postPhotoList = requestDto.getPostPhotoList().stream()
                .map(PhotoProvider::decode)
                .toList();

        // post 생성
        Post post = Post.builder()
                .course(schedule.getCourse())
                .content(requestDto.getContent())
                .title(requestDto.getTitle())
                .isPublic(requestDto.getIsPublic())
                .build();

        for (byte[] p : postPhotoList) {
            PostPhoto postPhoto = PostPhoto.builder()
                    .photo(p)
                    .build();
            // 포스트와 포스트 사진 리스트 연결
            post.addPostPhoto(postPhoto);
        }

        // user와 post 연결
        user.addPost(post);
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

    // 포스트 단건 조회
    public PostInfoDto findPost(Long userId, Long postId) {
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));
        Post post = user.getPosts().stream()
                .filter(p -> p.getId().equals(postId))
                .findAny()
                .orElseThrow(() -> new RuntimeException("스케줄 조회 실패"));

        return PostInfoDto.from(post);
    }

    // 포스트 3건 조회 - 최신순
    public List<PostInfoDto> findTop3PostOrderByCreatedAt() {
        Optional<List<Post>> postList = postRepository.findTop3ByOrderByCreatedAtDesc();

        List<PostInfoDto> postInfoDtoList = postList.get().stream()
                .map(PostInfoDto::from)
                .toList();

        return postInfoDtoList;
    }

    // 모든 포스트 최신순 조회
    public List<PostInfoDto> findAllOrderByCreatedAtDesc(){
        Optional<List<Post>> postList = postRepository.findAllOrderByCreatedAtDesc();
        List<PostInfoDto> postInfoDtoList = postList.get().stream()
                .map(PostInfoDto::from)
                .toList();
        return postInfoDtoList;
    }



    // 포스트 수정
    public void updatePost(Long userId, PostUpdateDto requestDto) {
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));

        // 코스 생성
        Course course = Course.builder().location(requestDto.getLocation()).build();

        // 장소리스트로 돌기
        for (SpaceResponseDto spaceResponseDto : requestDto.getSpaceList()) {

            // 1. SpaceDetail에 저장 - 캐시저장
            SpaceDetail spaceDetail = SpaceDetail.builder()
                    .googlePlacesIdentifier(spaceResponseDto.getGooglePlacesIdentifier())
                    .spaceName(spaceResponseDto.getName())
                    .location(spaceResponseDto.getLocation())
                    .spaceType(spaceResponseDto.getSpaceType())
                    .link(spaceResponseDto.getLink())
                    .latitude(spaceResponseDto.getLatitude())
                    .longitude(spaceResponseDto.getLongitude())
                    .build();

            // 장소 사진 디코드
            List<byte[]> spacePhotoList = spaceResponseDto.getSpacePhotos().stream()
                    .map(PhotoProvider::decode)
                    .toList();

            // 디코드된 사진 리스트로 SpacePhoto 생성
            for (byte[] sp : spacePhotoList) {
                SpacePhoto spacePhoto = SpacePhoto.builder().value(sp).build();
                // SpaceDetail과 연결
                spaceDetail.addSpacePhoto(spacePhoto);
            }

            // 2. Space 생성
            Space space = Space.builder()
                    .spaceDetail(spaceDetail)
                    .build();
            // 3. Course와 Space 연결
            course.addSpace(space);
        }

    }

    // 포스트 삭제
    public void deletePost(Long userId, Long postId) {
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));
        Post post = user.getPosts().stream()
                .filter(p -> p.getId().equals(postId))
                .findAny()
                .orElseThrow(() -> new RuntimeException("포스트 조회 실패"));
        user.removePost(post);
    }
}
