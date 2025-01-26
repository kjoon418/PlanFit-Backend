package success.planfit.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.dto.request.PlaceDetailRequestDto;
import success.planfit.dto.response.PlaceDetailResponseDto;
import success.planfit.service.PlaceDetailService;

import java.util.List;

@AllArgsConstructor
@RestController
public class PlaceDetailController {

    private final PlaceDetailService placeDetailService;

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


}
