package success.planfit.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.controller.utils.PlanfitExceptionHandler;
import success.planfit.dto.request.PlaceDetailRequestDto;
import success.planfit.dto.response.PlaceDetailResponseDto;
import success.planfit.service.PlaceDetailService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
public class PlaceDetailController {

    private final PlaceDetailService placeDetailService;
    private final PlanfitExceptionHandler exceptionHandler;

    // AI에게 placeId로 요청받는 경우
    @GetMapping("/place/detailList/{placeId}")
    public ResponseEntity<PlaceDetailResponseDto> placeDetail(
            @PathVariable String placeId
    ){
        return ResponseEntity.ok(placeDetailService.getPlaceDetailsById(placeId));
    }

    // 프론트에게 위도 경도 radius로 요청받는 경우
    @GetMapping("/place/nearByDetails")
    public ResponseEntity<List<PlaceDetailResponseDto>> placeDetails(
            @RequestBody PlaceDetailRequestDto requestDto
    ){
        return ResponseEntity.ok(placeDetailService.getPlaceDetailsByLocation(requestDto));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("PlaceDetailController.handleException() called");

        return exceptionHandler.handle(exception);
    }
}
