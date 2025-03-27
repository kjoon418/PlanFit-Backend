package success.planfit.rating.controller;

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
public class RatingController {

    private final ControllerUtil util;
    private final PlanfitExceptionHandler exceptionHandler;
    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<Void> recordRating(Principal principal, RatingRecordRequestDto requestDto) {
        log.info("RatingController.recordRating() called");

        long userId = util.findUserIdByPrincipal(principal);
        ratingService.recordRating(userId, requestDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping
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
