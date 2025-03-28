package success.planfit.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.course.dto.SpaceRequestDto;
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
import success.planfit.repository.SpaceDetailRepository;
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
    private final SpaceDetailRepository spaceDetailRepository;

    // 사용자가 코스 생성해서 포스팅
    public void registerPostByUser(Long userId, PostSaveRequestDtoByUser requestDto) {
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));

        // 코스 생성
        Course course = Course.builder().location(requestDto.getLocation()).build();
        // 장소리스트로 돌기 -> SpaceDetail로 space를 만들고 코슬 ㄹ만들어야함 그리고 그 포스트를 저장
        for (SpaceRequestDto spaceRequestDto : requestDto.getSpaces()) {
            // 1. googleIdentifier로 spaceDetail 가져오기
            SpaceDetail spaceDetail = spaceDetailRepository.findByGooglePlacesIdentifier(spaceRequestDto.getGooglePlacesIdentifier())
                    .orElseThrow(() -> new EntityNotFoundException("장소 조회 실패"));

            // 2. Space 생성
            Space space = Space.builder()
                    .spaceDetail(spaceDetail)
                    .sequence(spaceRequestDto.getSequence())
                    .build();
            // 3. Course와 Space 연결
            course.addSpace(space);
        }

        // dto -> 디코드한 postPhotos
        List<byte[]> postPhotos = requestDto.getPostPhotos().stream()
                .map(PhotoProvider::decode)
                .toList();

        // post 생성
        Post post = Post.builder()
                .course(course)
                .content(requestDto.getContent())
                .title(requestDto.getTitle())
                .isPublic(requestDto.getIsPublic())
                .build();

        for (byte[] photos : postPhotos) {
            PostPhoto postPhoto = PostPhoto.builder()
                    .photo(photos)
                    .build();
            // 포스트와 포스트 사진 리스트 연결
            post.addPostPhoto(postPhoto);
        }

        // user와 post 연결
        user.addPost(post);
    }

    // 사용자의 스케줄에서 불러와서 포스팅
    public void registerPostByScheduleId(Long userId, PostSaveRequestFromSchedule requestDto) {
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));
        // 스케줄 반환
        Schedule schedule = user.getSchedules().stream()
                .filter(sc -> sc.getId().equals(requestDto.getScheduleId()))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("스케줄 조회 실패"));

        List<byte[]> postPhotos = requestDto.getPostPhotos().stream()
                .map(PhotoProvider::decode)
                .toList();

        // post 생성
        Post post = Post.builder()
                .course(schedule.getCourse())
                .content(requestDto.getContent())
                .title(requestDto.getTitle())
                .isPublic(requestDto.getIsPublic())
                .build();

        for (byte[] photo : postPhotos) {
            PostPhoto postPhoto = PostPhoto.builder()
                    .photo(photo)
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
    public PostInfoDto findPost(Long postId) {
        Post post = postRepository.findById(postId).stream()
                .filter(photo -> photo.getId().equals(postId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("스케줄 조회 실패"));

        return PostInfoDto.from(post);
    }

    // 포스트 3건 조회 - 최신순
    public List<PostInfoDto> findTopNPostOrderByCreatedAt(int n) {
        Optional<List<Post>> posts = postRepository.findTop3ByOrderByCreatedAtDesc(n);

        List<PostInfoDto> postInfoDtos = posts.get().stream()
                .map(PostInfoDto::from)
                .toList();

        return postInfoDtos;
    }

    // 모든 포스트 최신순 조회
    public List<PostInfoDto> findAllOrderByCreatedAtDesc(){
        Optional<List<Post>> posts = postRepository.findAllOrderByCreatedAtDesc();
        List<PostInfoDto> postInfoDtos = posts.get().stream()
                .map(PostInfoDto::from)
                .toList();
        return postInfoDtos;
    }

    // 포스트 수정
    public void updatePost(Long userId, PostUpdateDto requestDto) {
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));

        // 코스 생성
        Course course = Course.builder().location(requestDto.getLocation()).build();

        // 장소리스트로 돌기
        for (SpaceResponseDto spaceResponseDto : requestDto.getSpaces()) {

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
            List<byte[]> spacePhotos = spaceResponseDto.getSpacePhotos().stream()
                    .map(PhotoProvider::decode)
                    .toList();

            // 디코드된 사진 리스트로 SpacePhoto 생성
            for (byte[] sp : spacePhotos) {
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
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));
        Post post = user.getPosts().stream()
                .filter(p -> p.getId().equals(postId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("포스트 조회 실패"));
        user.removePost(post);
    }
}
