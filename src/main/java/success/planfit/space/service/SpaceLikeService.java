package success.planfit.space.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.entity.like.SpaceLike;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.user.User;
import success.planfit.global.exception.EntityNotFoundException;
import success.planfit.repository.SpaceDetailRepository;
import success.planfit.repository.SpaceLikeRepository;
import success.planfit.repository.UserRepository;
import success.planfit.space.dto.response.SpaceLikeResponseDto;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SpaceLikeService {

    private final UserRepository userRepository;
    private final SpaceLikeRepository spaceLikeRepository;
    private final SpaceDetailRepository spaceDetailRepository;

    public void likeSpace(String googlePlacesIdentifier, Long userId) {
        SpaceDetail spaceDetail = spaceDetailRepository.findByGooglePlacesIdentifier(googlePlacesIdentifier)
                .orElseThrow(() -> new EntityNotFoundException("장소를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        if (spaceLikeRepository.existsByUserAndSpaceDetail(user, spaceDetail)) {
            throw new IllegalStateException("유저가 이미 좋아요 한 장소입니다.");
        }

        SpaceLike spaceLike = SpaceLike.builder()
                .spaceDetail(spaceDetail)
                .user(user)
                .build();

        spaceDetail.increaseLikeCount();
    }

    @Transactional(readOnly = true)
    public List<SpaceLikeResponseDto> getLikedSpaces(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        List<SpaceLikeResponseDto> spaceLikeResponseDtos = user.getSpaceLikes()
                .stream()
                .map((spaceLike) -> spaceLike.getSpaceDetail())
                .map((spaceDetail) -> new SpaceLikeResponseDto(spaceDetail))
                .toList();

        return spaceLikeResponseDtos;

    }

    public void unlikeSpace(String googlePlacesIdentifier, Long userId) {
        SpaceDetail spaceDetail = spaceDetailRepository.findByGooglePlacesIdentifier(googlePlacesIdentifier)
                .orElseThrow(() -> new EntityNotFoundException("장소를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        SpaceLike spaceLike = spaceLikeRepository.findByUserAndSpaceDetail(user, spaceDetail)
                .orElseThrow(() -> new EntityNotFoundException("좋아요를 찾을 수 없습니다."));

        spaceLikeRepository.delete(spaceLike);
        spaceDetail.decreaseLikeCount();
        spaceDetailRepository.save(spaceDetail);
    }

}
