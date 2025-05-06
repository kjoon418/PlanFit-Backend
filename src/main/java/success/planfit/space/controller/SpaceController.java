package success.planfit.space.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.course.dto.SpaceDetailInfoDto;
import success.planfit.course.dto.SpaceRequestDto;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.global.controller.PlanfitExceptionHandler;
import success.planfit.space.dto.request.SpaceDetailRequestDto;
import success.planfit.space.dto.response.SpaceResponseFromAI;
import success.planfit.space.dto.request.SpaceInfoForAIDto;
import success.planfit.space.service.SpaceService;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/space")
@Tag(
        name = "장소 API"
)
public class SpaceController {

    private final ControllerUtil controllerUtil;
    private final PlanfitExceptionHandler exceptionHandler;
    private final SpaceService spaceService;

    @GetMapping("/toAI")
    @Operation(
            summary = "AI에게 장소 조회 요청"
    )
    public ResponseEntity<SpaceInfoForAIDto> requestToAI(Principal principal,
                                                         @RequestBody SpaceDetailRequestDto requestDto) {
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        SpaceInfoForAIDto spaceInfoForAIDto = spaceService.requestToAI(userId, requestDto);
        return ResponseEntity.ok(spaceInfoForAIDto);
    }

    @GetMapping("/toFront")
    @Operation(
            summary = "AI에게 장소 받아서 갱신, 정렬 후, 프론트 장소 리스트에게 전달"
    )
    public ResponseEntity<List<SpaceDetailInfoDto>> responseToFront(Principal principal,
                                                                    @RequestBody List<SpaceResponseFromAI> requestDtoList) {
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        List<SpaceDetailInfoDto> spaceResponseDtoList = spaceService.responseToFE(requestDtoList);
        return ResponseEntity.ok(spaceResponseDtoList);
    }

    @GetMapping("/user")
    @Operation(
            summary = "사용자가 직접 장소들 고르기"
    )
    public ResponseEntity<List<SpaceRequestDto>> findSpacesFromUser(@RequestBody List<SpaceRequestDto> requestDtos) {
        List<SpaceRequestDto> spacesFromUser = spaceService.getSpacesFromUser(requestDtos);
        return ResponseEntity.ok(spacesFromUser);
    }

    @GetMapping("/{scheduleId}")
    @Operation(
            summary = "스케줄 참조해서 그 안에 있는 코스의 장소 리스트"
    )
    public ResponseEntity<List<SpaceRequestDto>> findSpacesFromSchedule(
            Principal principal,
            @PathVariable long scheduleId
    ) {
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        List<SpaceRequestDto> spacesFromSchedule = spaceService.getSpacesFromSchedule(userId, scheduleId);
        return ResponseEntity.ok(spacesFromSchedule);
    }

    @GetMapping("/{spaceDetailId}")
    @Operation(
            summary = "장소 단건 조회"
    )
    public ResponseEntity<SpaceDetailInfoDto> findSpaceDetailInfo(long spaceDetailId) {
        SpaceDetailInfoDto spaceDetailInfo = spaceService.findSpaceDetailInfo(spaceDetailId);
        return ResponseEntity.ok(spaceDetailInfo);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("UserController.handleException() called");

        return exceptionHandler.handle(exception);
    }

}
