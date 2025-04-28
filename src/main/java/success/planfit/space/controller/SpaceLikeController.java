package success.planfit.space.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.space.dto.request.SpaceLikeRequestDto;
import success.planfit.space.dto.response.SpaceLikeResponseDto;
import success.planfit.space.service.SpaceLikeService;

import java.util.List;

@RestController
@RequestMapping("/spaces")
@RequiredArgsConstructor
public class SpaceLikeController {
    private final SpaceLikeService spaceLikeService;

    /**
     * 장소 좋아요 등록
     */
    @PostMapping("/like")
    public ResponseEntity<Void> likeSpace(@RequestBody SpaceLikeRequestDto requestDto) {
        spaceLikeService.likeSpace(requestDto.getGooglePlacesIdentifier(), requestDto.getUserId());
        return ResponseEntity.ok().build();
    }

    /**
     * 장소 좋아요 조회
     */
    @GetMapping("/liked/{userId}")
    public ResponseEntity<List<SpaceLikeResponseDto>> getLikedSpaces(@PathVariable Long userId) {
        List<SpaceLikeResponseDto> likedSpaces = spaceLikeService.getLikedSpaces(userId);
        return ResponseEntity.ok(likedSpaces);
    }

    /**
     * 장소 좋아요 취소
     */
    @DeleteMapping("/like")
    public ResponseEntity<Void> unlikeSpace(@RequestBody SpaceLikeRequestDto requestDto) {
        spaceLikeService.unlikeSpace(requestDto.getGooglePlacesIdentifier(), requestDto.getUserId());
        return ResponseEntity.ok().build();
    }

}
