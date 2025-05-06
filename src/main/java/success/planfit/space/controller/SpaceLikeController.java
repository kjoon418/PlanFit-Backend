package success.planfit.space.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "장소 좋아요 API",
        description = "등록, 조회, 취소 기능"
)
public class SpaceLikeController {
    private final SpaceLikeService spaceLikeService;

    /**
     * 장소 좋아요 등록
     */
    @PostMapping("/like")
    @Operation(
            summary = "장소 좋아요 등록",
            description = "마음에 든 장소를 리스트에 저장합니다"
    )
    public ResponseEntity<Void> likeSpace(@RequestBody SpaceLikeRequestDto requestDto) {
        spaceLikeService.likeSpace(requestDto.getGooglePlacesIdentifier(), requestDto.getUserId());
        return ResponseEntity.ok().build();
    }

    /**
     * 장소 좋아요 조회
     */
    @GetMapping("/liked/{userId}")
    @Operation(
            summary = "장소 좋아요 조회",
            description = "좋아요한 장소들의 리스트를 불러옵니다."
    )
    public ResponseEntity<List<SpaceLikeResponseDto>> getLikedSpaces(@PathVariable Long userId) {
        List<SpaceLikeResponseDto> likedSpaces = spaceLikeService.getLikedSpaces(userId);
        return ResponseEntity.ok(likedSpaces);
    }

    /**
     * 장소 좋아요 취소
     */
    @DeleteMapping("/like")
    @Operation(
            summary = "장소 좋아요 취소",
            description = "마음에 든 장소를 리스트에서 제거합니다."
    )
    public ResponseEntity<Void> unlikeSpace(@RequestBody SpaceLikeRequestDto requestDto) {
        spaceLikeService.unlikeSpace(requestDto.getGooglePlacesIdentifier(), requestDto.getUserId());
        return ResponseEntity.ok().build();
    }

}
