package success.planfit.rating.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.global.controller.PlanfitExceptionHandler;
import success.planfit.rating.dto.RatingRecordRequestDto;
import success.planfit.rating.service.RatingService;

import java.security.Principal;

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
        log.info("RatingController.recordRating()");

        Long userId = util.findUserIdByPrincipal(principal);
        ratingService.recordRating(userId, requestDto);

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("RatingController.handleException()");

        return exceptionHandler.handle(exception);
    }

}
