package success.planfit.rating.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.global.controller.PlanfitExceptionHandler;
import success.planfit.rating.dto.RatingRecordRequestDto;
import success.planfit.rating.service.RatingService;
import success.planfit.schedule.dto.response.ScheduleResponseDto;

import java.security.Principal;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("rating")
@RequiredArgsConstructor
@Tag(
        name = "별점(리뷰) API",
        description = "별점(리뷰) 관련 기능"
)
public class RatingController {

    private final ControllerUtil util;
    private final PlanfitExceptionHandler exceptionHandler;
    private final RatingService ratingService;

    @PostMapping
    @Operation(
            summary = "별점 기록",
            description = "해당 일정(Schedule)에 대한 새로운 별점을 등록합니다"
    )
    public ResponseEntity<Void> recordRating(Principal principal, RatingRecordRequestDto requestDto) {
        log.info("RatingController.recordRating() called");

        long userId = util.findUserIdByPrincipal(principal);
        ratingService.recordRating(userId, requestDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(
            summary = "별점 요청이 가능한 일정 조회",
            description = "별점 요청이 가능한 일정 하나를 반환합니다. 한번 반환된 일정은 다시 반환되지 않습니다."
    )
    public ResponseEntity<ScheduleResponseDto> findRatingRequestAvailableSchedule(Principal principal, LocalDate date) {
        log.info("RatingController.findRatingRequestAvailableSchedule() called");

        long userId = util.findUserIdByPrincipal(principal);
        ScheduleResponseDto responseDto = ratingService.getRatingRequestAvailableSchedule(userId, date);

        return ResponseEntity.ok(responseDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("RatingController.handleException()");

        return exceptionHandler.handle(exception);
    }

}
