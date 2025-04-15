package success.planfit.space.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.course.dto.SpaceDetailInfoDto;
import success.planfit.course.dto.SpaceRequestDto;
import success.planfit.course.dto.SpaceResponseDto;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.space.dto.request.SpaceDetailRequestDto;
import success.planfit.space.dto.request.SpaceRequestFromAI;
import success.planfit.space.dto.response.SpaceInfoForAIDto;
import success.planfit.space.service.SpaceService;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/space")
public class SpaceController {

    private final ControllerUtil controllerUtil;
    private final SpaceService spaceService;

    /**
     * AI에게 장소 조회 요청
     */
    @GetMapping("/toAI")
    public ResponseEntity<SpaceInfoForAIDto> requestToAI(Principal principal, @RequestBody SpaceDetailRequestDto requestDto){
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        SpaceInfoForAIDto spaceInfoForAIDto = spaceService.requestToAI(userId, requestDto);
        return ResponseEntity.ok(spaceInfoForAIDto);
    }

    /**
     * AI에게 장소 받아서 갱신, 정렬 후, 프론트 장소 리스트에게 전달
     */
    @GetMapping("/toFront")
    public ResponseEntity<List<SpaceDetailInfoDto>> responseToFront(Principal principal, @RequestBody List<SpaceRequestFromAI> requestDtoList){
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        List<SpaceDetailInfoDto> spaceResponseDtoList = spaceService.responseToFE(requestDtoList);
        return ResponseEntity.ok(spaceResponseDtoList);
    }

    /**
     * 사용자가 직접 장소들 고르기
     */
    @GetMapping("/user")
    public ResponseEntity<List<SpaceRequestDto>> findSpacesFromUser(@RequestBody List<SpaceRequestDto> requestDtos){
        List<SpaceRequestDto> spacesFromUser = spaceService.getSpacesFromUser(requestDtos);
        return ResponseEntity.ok(spacesFromUser);
    }

    /**
     * 스케줄 참조해서 그 안에 있는 코스의 장소 리스트
     */
    @GetMapping("/{scheduleId}")
    public ResponseEntity<List<SpaceRequestDto>> findSpacesFromSchedule(
            Principal principal,
            @PathVariable Long scheduleId
    ){
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        List<SpaceRequestDto> spacesFromSchedule = spaceService.getSpacesFromSchedule(userId, scheduleId);
        return ResponseEntity.ok(spacesFromSchedule);
    }

    /**
     * 장소 단건 조회
     */
    @GetMapping("/{spaceDetailId}")
    public ResponseEntity<SpaceDetailInfoDto> findSpaceDetailInfo(Long spaceDetailId){
        SpaceDetailInfoDto spaceDetailInfo = spaceService.findSpaceDetailInfo(spaceDetailId);
        return ResponseEntity.ok(spaceDetailInfo);
    }
}
