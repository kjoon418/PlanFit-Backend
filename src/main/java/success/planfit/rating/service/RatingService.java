package success.planfit.rating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.entity.space.Rating;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.user.User;
import success.planfit.global.exception.EntityNotFoundException;
import success.planfit.rating.dto.RatingRecordRequestDto;
import success.planfit.repository.SpaceDetailRepository;
import success.planfit.repository.UserRepository;

import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RatingService {

    private static final Supplier<EntityNotFoundException> USER_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("해당 ID를 통해 유저를 조회할 수 없습니다.");
    private static final Supplier<EntityNotFoundException> SPACE_DETAIL_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("해당 Google places identifier로 장소를 조회할 수 없습니다.");

    private final UserRepository userRepository;
    private final SpaceDetailRepository spaceDetailRepository;

    @Transactional
    public void recordRating(Long userId, RatingRecordRequestDto requestDto) {
        User user = getUser(userId);
        SpaceDetail spaceDetail = getSpaceDetail(requestDto.googlePlacesIdentifier());

        Optional<Rating> existsRating = getExistsRating(user, spaceDetail);

        if (existsRating.isPresent()) {
            updateRating(existsRating.get(), requestDto);
            return;
        }

        Rating rating = createRating(user, spaceDetail, requestDto.rating());
        connectEntities(user, spaceDetail, rating);
    }

    private User getUser(Long userId) {
        return userRepository.findByIdWithRatings(userId)
                .orElseThrow(USER_NOT_FOUND_EXCEPTION);
    }

    private SpaceDetail getSpaceDetail(String googlePlacesIdentifier) {
        return spaceDetailRepository.findByGooglePlacesIdentifier(googlePlacesIdentifier)
                .orElseThrow(SPACE_DETAIL_NOT_FOUND_EXCEPTION);
    }

    private Optional<Rating> getExistsRating(User user, SpaceDetail spaceDetail) {
        return user.getRatings().stream()
                .filter(rating -> rating.getSpaceDetail().equals(spaceDetail))
                .filter(rating -> rating.getUser().equals(user))
                .findAny();
    }

    private Rating createRating(User user, SpaceDetail spaceDetail, Integer value) {
        return Rating.builder()
                .user(user)
                .spaceDetail(spaceDetail)
                .value(value)
                .build();
    }

    private void updateRating(Rating rating, RatingRecordRequestDto requestDto) {
        int value = requestDto.rating();

        rating.setValue(value);
    }

    private void connectEntities(User user, SpaceDetail spaceDetail, Rating rating) {
        user.addRating(rating);
        spaceDetail.addRating(rating);
    }

}
