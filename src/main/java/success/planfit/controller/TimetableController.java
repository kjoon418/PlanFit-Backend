package success.planfit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import success.planfit.controller.utils.ControllerUtil;
import success.planfit.dto.request.TimetableCreationRequestDto;
import success.planfit.dto.request.TimetableUpdateRequestDto;
import success.planfit.dto.response.TimetableInfoResponseDto;
import success.planfit.service.TimetableService;


import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/timetable")
public class TimetableController {

    private final ControllerUtil controllerUtil;
    private final TimetableService timetableService;


    // 날짜를 기준으로 시간표 등록
    @PostMapping("/{date}")
    public ResponseEntity<Void> addTimetable(@PathVariable LocalDate date, @RequestBody TimetableCreationRequestDto requestDto, Principal principal) {
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        timetableService.addTimetable(userId,requestDto,date);
        return ResponseEntity.ok().build();
    }

    // 날짜를 기준으로 시간표 삭제
    @DeleteMapping("/{date}/{timetableId}")
    public ResponseEntity<String> removeTimetable(@PathVariable LocalDate date, Principal principal, @PathVariable Long timetableId) {
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        timetableService.removeTimetable(userId, date ,timetableId);
        return ResponseEntity.ok("Deleted successfully");
    }

    // 날짜를 기준으로 시간표 수정
    @PatchMapping("/{date}/{timetableId}")
    public ResponseEntity<TimetableInfoResponseDto> updateTimetable(@PathVariable LocalDate date, @PathVariable Long timetableId, Principal principal, @RequestBody TimetableUpdateRequestDto updateRequestDto ) {
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        return ResponseEntity.ok(timetableService.updateTimetable(userId, date, timetableId,updateRequestDto));
    }

    // 특정 날짜의 시간표 조회
    @GetMapping("/{date}")
    public ResponseEntity<List<TimetableInfoResponseDto>> getTimetables(@PathVariable LocalDate date, Principal principal) {
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        return ResponseEntity.ok(timetableService.getTimetables(userId, date));
    }
}
