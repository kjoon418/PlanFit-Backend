package success.planfit.space.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.global.controller.PlanfitExceptionHandler;
import success.planfit.space.dto.request.PlaceDetailRequestDto;
import success.planfit.space.dto.request.PlaceRelevanceDetail;
import success.planfit.space.dto.response.LocationDetailResponseDto;
import success.planfit.space.dto.response.PlaceDetailResponseDto;
import success.planfit.space.service.PlaceDetailService;

import java.security.Principal;

@Slf4j
@AllArgsConstructor
@RestController
public class PlaceDetailController {

    private final PlaceDetailService placeDetailService;
    private final PlanfitExceptionHandler exceptionHandler;
    private final ControllerUtil controllerUtil;

    // AI에게 placeId로 요청받는 경우
    @GetMapping("/place/detailList/")
    public ResponseEntity<PlaceDetailResponseDto> placeDetail(
            @RequestBody PlaceRelevanceDetail placeRelevanceDetail
    ){
        return ResponseEntity.ok(placeDetailService.getPlaceDetailsById(placeRelevanceDetail));
    }

    // 프론트에게 위도 경도 radius로 요청받는 경우
    @GetMapping("/place/nearByDetails")
    public ResponseEntity<LocationDetailResponseDto> placeDetails(
            Principal principal
            ,@RequestBody PlaceDetailRequestDto requestDto
    ){
        Long id = controllerUtil.findUserIdByPrincipal(principal);
        return ResponseEntity.ok(placeDetailService.passPlaceDetail(id, requestDto));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("PlaceDetailController.handleException() called");

        return exceptionHandler.handle(exception);
    }
}
