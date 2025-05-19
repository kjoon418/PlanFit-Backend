package success.planfit.schedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.global.controller.PlanfitExceptionHandler;
import success.planfit.schedule.dto.ShareSerialDto;
import success.planfit.schedule.dto.request.ScheduleCurrentSequenceUpdateRequestDto;
import success.planfit.schedule.dto.request.ScheduleRequestDto;
import success.planfit.schedule.dto.response.ScheduleResponseDto;
import success.planfit.schedule.dto.response.ScheduleTitleInfoResponseDto;
import success.planfit.schedule.service.ScheduleService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("schedule")
@AllArgsConstructor
@Tag(
        name = "일정 API",
        description = "일정 조회/생성/관리 관련 기능"
)
public class ScheduleController {

    private final ControllerUtil util;
    private final PlanfitExceptionHandler exceptionHandler;
    private final ScheduleService scheduleService;

    @PostMapping
    @Operation(
            summary = "일정 등록",
            description = "새로운 일정을 생성합니다"
    )
    public ResponseEntity<Void> registerSchedule(Principal principal, @RequestBody ScheduleRequestDto requestDto) {
        log.info("ScheduleController.registerSchedule() called");

        long userId = util.findUserIdByPrincipal(principal);
        scheduleService.registerSchedule(userId, requestDto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{scheduleId}")
    @Operation(
            summary = "일정 삭제",
            description = "일정 ID를 통해 해당 일정을 삭제합니다"
    )
    public ResponseEntity<Void> deleteSchedule(Principal principal, @PathVariable long scheduleId) {
        log.info("ScheduleController.deleteSchedule() called");

        long userId = util.findUserIdByPrincipal(principal);
        scheduleService.deleteSchedule(userId, scheduleId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/past")
    @Operation(
            summary = "지난 일정 조회",
            description = "지난 일정을 모두 조회합니다. 시간 순으로 내림차순 정렬된 컬렉션을 반환합니다."
    )
    public ResponseEntity<List<ScheduleTitleInfoResponseDto>> findPastSchedules(Principal principal) {
        log.info("ScheduleController.findPastSchedules() called");

        long userId = util.findUserIdByPrincipal(principal);
        List<ScheduleTitleInfoResponseDto> responseDtos = scheduleService.findPastSchedules(userId, LocalDate.now());

        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/upcoming")
    @Operation(
            summary = "다가올 일정 조회",
            description = "아직 지나지 않은 일정을 모두 조회합니다. 시간 순으로 오름차순 정렬된 컬렉션을 반환합니다."
    )
    public ResponseEntity<List<ScheduleTitleInfoResponseDto>> findUpcomingSchedules(Principal principal) {
        log.info("ScheduleController.findUpcomingSchedules() called");

        long userId = util.findUserIdByPrincipal(principal);
        List<ScheduleTitleInfoResponseDto> responseDtos = scheduleService.findUpcomingSchedules(userId, LocalDate.now());

        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/{scheduleId}")
    @Operation(
            summary = "일정 상세 조회",
            description = "하나의 일정에 대한 상세 정보를 반환합니다"
    )
    public ResponseEntity<ScheduleResponseDto> findScheduleDetail(Principal principal, @PathVariable long scheduleId) {
        log.info("ScheduleController.findScheduleDetail() called");

        long userId = util.findUserIdByPrincipal(principal);
        ScheduleResponseDto responseDto = scheduleService.findScheduleDetail(userId, scheduleId);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{scheduleId}")
    @Operation(
            summary = "일정 수정",
            description = "해당 일정에 대한 정보를 전달받은 정보로 덮어씁니다"
    )
    public ResponseEntity<Void> updateSchedule(Principal principal, @PathVariable long scheduleId, ScheduleRequestDto requestDto) {
        log.info("ScheduleController.updateSchedule() called");

        long userId = util.findUserIdByPrincipal(principal);
        scheduleService.update(userId, scheduleId, requestDto);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/visit")
    @Operation(
            summary = "장소 방문 기록",
            description = "해당 일정의 장소를 어디까지 방문했는지를 기록합니다"
    )
    public ResponseEntity<Void> visitScheduleSpace(Principal principal, ScheduleCurrentSequenceUpdateRequestDto requestDto) {
        log.info("ScheduleController.visitScheduleSpace() called");

        long userId = util.findUserIdByPrincipal(principal);
        scheduleService.updateCurrentSequence(userId, requestDto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/share/{scheduleId}")
    @Operation(
            summary = "일정 공유 시리얼 생성",
            description = "일정을 공유할 수 있는 시리얼을 반환합니다"
    )
    public ResponseEntity<ShareSerialDto> createShareSerial(Principal principal, @PathVariable Long scheduleId) {
        log.info("ScheduleController.createShareSerial() called");

        long userId = util.findUserIdByPrincipal(principal);
        ShareSerialDto shareSerialDto = scheduleService.createShareSerial(userId, scheduleId);

        return ResponseEntity.ok(shareSerialDto);
    }

    @GetMapping("/share/view/{shareSerial}")
    @Operation(
            summary = "일정 공유 시리얼을 통해 일정 조회",
            description = "공유받은 시리얼을 통해 일정을 조회합니다. 별도의 인증(JWT 토큰)을 필요로 하지 않습니다."
    )
    public ResponseEntity<ScheduleResponseDto> findScheduleByShareSerial(@PathVariable String shareSerial) {
        log.info("ScheduleController.findScheduleByShareSerial() called");

        ScheduleResponseDto responseDto = scheduleService.findByShareSerial(shareSerial);

        return ResponseEntity.ok(responseDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.info("ScheduleController.handleException() called");

        return exceptionHandler.handle(exception);
    }

}
