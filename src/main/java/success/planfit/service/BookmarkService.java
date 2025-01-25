package success.planfit.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.domain.bookmark.SpaceBookmark;
import success.planfit.domain.user.User;
import success.planfit.dto.request.SpaceBookmarkDeleteRequestDto;
import success.planfit.dto.request.SpaceBookmarkRegistrationRequestDto;
import success.planfit.dto.response.SpaceBookmarkInfoResponseDto;
import success.planfit.repository.UserRepository;

import java.util.List;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkService {

    private final UserRepository userRepository;

    public void registerSpaceBookmark(Long userId, SpaceBookmarkRegistrationRequestDto requestDto) {
        log.info("BookmarkService.registerSpaceBookmark() called");

        // 엔티티 조회 및 생성
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 조회 실패"));
        SpaceBookmark spaceBookmark = requestDto.toEntity();

        // 연관관계 편의 메서드 사용
        user.addSpaceBookmark(spaceBookmark);
    }

    public void deleteSpaceBookmark(Long userId, SpaceBookmarkDeleteRequestDto requestDto) {
        log.info("BookmarkService.deleteSpaceBookmark() called");

        // 엔티티 조회
        User user = userRepository.findByIdWithSpaceBookmark(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 조회 실패"));
        SpaceBookmark spaceBookmark = user.getSpaceBookmarks().stream()
                .filter(spacebookmark -> spacebookmark.getGooglePlacesIdentifier().equals(requestDto.getGooglePlacesIdentifier()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("좋아요에 등록된 장소 조회 실패"));

        // 연관관계 편의 메서드 사용
        user.removeSpaceBookmark(spaceBookmark);
    }

    @Transactional(readOnly = true)
    public List<SpaceBookmarkInfoResponseDto> findAllSpaceBookmarks(Long userId) {
        log.info("BookmarkService.findAllSpaceBookmarks() called");

        System.out.println("userId = " + userId);

        // 엔티티 조회
        User user = userRepository.findByIdWithSpaceBookmark(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 조회 실패"));

        // DTO로 변환하여 반환
        return user.getSpaceBookmarks().stream()
                .map(SpaceBookmarkInfoResponseDto::of)
                .toList();
    }
}
