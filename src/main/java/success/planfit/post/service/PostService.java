package success.planfit.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.course.dto.SpaceRequestDto;
import success.planfit.entity.comment.Comment;
import success.planfit.entity.course.Course;
import success.planfit.entity.post.Post;
import success.planfit.entity.post.PostPhoto;
import success.planfit.entity.post.PostType;
import success.planfit.entity.post.PostTypeValue;
import success.planfit.entity.space.Space;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.user.User;
import success.planfit.global.photo.PhotoProvider;
import success.planfit.post.dto.request.PostRequestDto;
import success.planfit.post.dto.response.PostInfoDto;
import success.planfit.course.dto.CourseResponseDto;
import jakarta.persistence.EntityNotFoundException;
import success.planfit.global.exception.IllegalRequestException;
import success.planfit.repository.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;


@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class PostService {
    private static final Supplier<EntityNotFoundException> USER_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("유저 조회에 실패했습니다.");
    private static final Supplier<EntityNotFoundException> POST_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("해당 ID를 지닌 포스트를 찾을 수 없습니다.");
    private static final int PAGE_SIZE = 10;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SpaceDetailRepository spaceDetailRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostLikeRepository postLikeRepository;

    public void registerPost(long userId, PostRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(USER_NOT_FOUND_EXCEPTION);

        Post post = createPost(requestDto);
        Course course = createCourse(requestDto);
        List<Space> spaces = createSpaces(requestDto.getSpaces());
        List<PostPhoto> postPhotos = createPostPhoto(requestDto.getPostPhotos());
        List<PostType> postTypes = createPostType(requestDto.getPostTypes());

        connectEntities(user, post, course, spaces, postPhotos, postTypes);
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public CourseResponseDto findCourseInPublicPost(long postId) {
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

    @Transactional(readOnly = true)
    public PostInfoDto findPost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(POST_NOT_FOUND_EXCEPTION);

        return PostInfoDto.from(post);
    }

    @Transactional(readOnly = true)
    public List<PostInfoDto> findRecentPosts(int n) {
        List<Post> posts = postRepository.findTopNByOrderByCreatedAtDesc(n)
                .orElseThrow(POST_NOT_FOUND_EXCEPTION);

        List<PostInfoDto> postInfoDtos = posts.stream()
                .map(PostInfoDto::from)
                .toList();

        return postInfoDtos;
    }

    @Transactional(readOnly = true)
    public List<PostInfoDto> findAll(int pageNo, String criteria){
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE,Sort.by(Sort.Direction.DESC,criteria));
        Page<PostInfoDto> page = postRepository.findAll(pageable)
                .map(PostInfoDto::from);
        return page.getContent();
    }

    public void updatePost(long userId, long postId, PostRequestDto requestDto) {
        Post post = postRepository.findByIdWithUserAndCourseAndComment(postId).stream()
                .filter(postForFilter -> postForFilter.getUser().getId().equals(userId))
                .findAny()
                .orElseThrow(POST_NOT_FOUND_EXCEPTION);

        List<Space> spaces = createSpaces(requestDto.getSpaces());
        Course course = post.getCourse();
        List<PostPhoto> postPhotos = createPostPhoto(requestDto.getPostPhotos());
        List<PostType> postTypes = createPostType(requestDto.getPostTypes());

        course.update(requestDto.getLocation());
        post.update(requestDto);
        replaceSpaces(course, spaces);
        replacePostPhotoAndPost(post, postPhotos, postTypes);
    }

    public void deletePost(long userId, long postId) {
        User user = userRepository.findByIdWithPost(userId)
                .orElseThrow(USER_NOT_FOUND_EXCEPTION);
        Post post = user.getPosts().stream()
                .filter(p -> p.getId().equals(postId))
                .findAny()
                .orElseThrow(POST_NOT_FOUND_EXCEPTION);
        List<Long> commentIds = post.getComments().stream()
                .map(Comment::getId)
                .toList();

        commentLikeRepository.deleteAllByCommentIdIn(commentIds);
        postLikeRepository.findByUserIdAndPostId(userId, postId).stream()
                .forEach(user::removePostLike);
        user.removePost(post);
    }

    private static Post createPost(PostRequestDto requestDto) {
        return Post.builder()
                .content(requestDto.getContent())
                .title(requestDto.getTitle())
                .isPublic(requestDto.getIsPublic())
                .build();
    }

    private Course createCourse(PostRequestDto requestDto) {
        return Course.builder()
                .location(requestDto.getLocation())
                .build();
    }

    private List<Space> createSpaces(List<SpaceRequestDto> requestDto){
        ArrayList<Space> spaces = new ArrayList<>();

        int sequence = 0;
        for (SpaceRequestDto spaceRequestDto : requestDto) {
            SpaceDetail spaceDetail = spaceDetailRepository.findByGooglePlacesIdentifier(spaceRequestDto.getGooglePlacesIdentifier())
                    .orElseThrow(POST_NOT_FOUND_EXCEPTION);
            spaces.add(Space.createSpace(spaceDetail, sequence));

            sequence++;
        }
        return Collections.unmodifiableList(spaces);
    }

    private List<PostPhoto> createPostPhoto(List<String> postPhotos){
        return postPhotos.stream()
                .map(PhotoProvider::decode)
                .map(postPhoto -> {
                    return PostPhoto.builder()
                            .photo(postPhoto)
                            .build();
                })
                .toList();
    }

    private List<PostType> createPostType(List<String> postTypes){
        return postTypes.stream()
                .map(postType -> {
                    return PostType.builder()
                            .value(PostTypeValue.valueOf(postType))
                            .build();
                })
                .toList();
    }

    private void connectEntities(User user, Post post, Course course, List<Space> spaces
                                ,List<PostPhoto> postPhotos, List<PostType> postTypes) {
        course.addSpaces(spaces);
        post.addPostTypes(postTypes);
        post.addPostPhotos(postPhotos);
        post.setCourse(course);
        user.addPost(post);
    }

    private void replaceSpaces(Course course, List<Space> spaces) {
        course.removeEverySpace();
        course.addSpaces(spaces);
    }

    private void replacePostPhotoAndPost(Post post, List<PostPhoto> postPhotos, List<PostType> postTypes) {
        post.removeEveryPostPhotos();
        post.removeEveryPostTypes();
        post.addPostPhotos(postPhotos);
        post.addPostTypes(postTypes);
    }
}
