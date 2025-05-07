package success.planfit.space.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.course.dto.SpaceDetailInfoDto;
import success.planfit.course.dto.SpaceRequestDto;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.global.controller.PlanfitExceptionHandler;
import success.planfit.space.dto.request.SpaceDetailRequestDto;
import success.planfit.space.service.SpaceService;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/space")
@Tag(
        name = "장소 API"
)
public class SpaceController {

    private final ControllerUtil controllerUtil;
    private final PlanfitExceptionHandler exceptionHandler;
    private final SpaceService spaceService;

    /**
     * AI에게 장소 조회 요청 갱신, 정렬 후,
     * 프론트 장소 리스트에게 전달
     */
    @Operation(
            summary = "AI에게 장소 조회 요청"
    )
    @GetMapping("/getSpaceDetails")
    public ResponseEntity<List<SpaceDetailInfoDto>> getSpaceDetails(@RequestBody SpaceDetailRequestDto requestDto) {
        List<SpaceDetailInfoDto> spaceDetailInfoDtos = spaceService.getSpaceDetails(requestDto);
        return ResponseEntity.ok(spaceDetailInfoDtos);
    }
    /**
     * 사용자가 직접 장소들 고르기
     */
    @GetMapping("/user")
    @Operation(
            summary = "사용자가 직접 장소들 고르기"
    )
    public ResponseEntity<List<SpaceRequestDto>> findSpacesFromUser(@RequestBody List<SpaceRequestDto> requestDtos) {
        List<SpaceRequestDto> spacesFromUser = spaceService.getSpacesFromUser(requestDtos);
        return ResponseEntity.ok(spacesFromUser);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<List<SpaceRequestDto>> findSpacesFromSchedule(
            Principal principal,
            @PathVariable long scheduleId
    ) {
        long userId = controllerUtil.findUserIdByPrincipal(principal);
        List<SpaceRequestDto> spacesFromSchedule = spaceService.getSpacesFromSchedule(userId, scheduleId);
        return ResponseEntity.ok(spacesFromSchedule);
    }

    @GetMapping("/{spaceDetailId}")
    public ResponseEntity<SpaceDetailInfoDto> findSpaceDetailInfo(long spaceDetailId){
        SpaceDetailInfoDto spaceDetailInfo = spaceService.findSpaceDetailInfo(spaceDetailId);
        return ResponseEntity.ok(spaceDetailInfo);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("UserController.handleException() called");

        return exceptionHandler.handle(exception);
    }

}
