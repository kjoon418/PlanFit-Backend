package success.planfit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.domain.course.Calendar;
import success.planfit.domain.course.Timetable;
import success.planfit.domain.embeddable.SpaceInformation;
import success.planfit.domain.user.User;
import success.planfit.dto.request.TimetableCreationRequestDto;
import success.planfit.dto.request.TimetableUpdateRequestDto;
import success.planfit.dto.response.TimetableInfoResponseDto;
import success.planfit.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class TimetableService {

    private final UserRepository userRepository;

    public void addTimetable(Long userId, TimetableCreationRequestDto requestDto, LocalDate date ) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));
        Calendar calendar = user.getCalendars().stream().filter(cal -> cal.getDate().equals(date)).findFirst().orElseGet(() -> Calendar.builder().date(date).title(date.toString()).build());
        user.addCalendar(calendar);
        Timetable timetable = requestDto.toEntity();
        calendar.addTimetable(timetable);
    }

    public void removeTimetable(Long userId, LocalDate date, Long timetableId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("유저 조회 실패"));
        Calendar calendar = user.getCalendars().stream().filter(cal -> cal.getDate().equals(date)).findFirst().orElseThrow(() -> new RuntimeException("캘린더 조회 실패"));
        Timetable timetable = calendar.getTimetables().stream().filter(timetb -> timetb.getId().equals(timetableId)).findFirst().orElseThrow(() -> new RuntimeException("타임 테이블 조회 실패"));
        calendar.removeTimetable(timetable);
    }

    public TimetableInfoResponseDto updateTimetable(Long userId, LocalDate date, Long timetableId, TimetableUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));
        Calendar calendar = user.getCalendars().stream()
                .filter(cal -> cal.getDate().equals(date))
                .findAny()
                .orElseThrow(() -> new RuntimeException("캘린더 조회 실패"));
        Timetable timetable = calendar.getTimetables().stream()
                .filter(timetb -> timetb.getId().equals(timetableId))
                .findAny()
                .orElseThrow(() -> new RuntimeException("타임 테이블 조회 실패"));

        timetable.setMemo(requestDto.getMemo() != null ? requestDto.getMemo() : timetable.getMemo());
        SpaceInformation existsSpaceInfo = timetable.getSpaceInformation();
        SpaceInformation newSpaceInfo = existsSpaceInfo.copyNotNulls(requestDto.getSpaceInformation());
        timetable.setSpaceInformation(newSpaceInfo);

        return TimetableInfoResponseDto.from(timetable);
    }

    public List<TimetableInfoResponseDto> getTimetables(Long userId, LocalDate date) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));

        return  user.getCalendars().stream()
                .filter(cal -> cal.getDate().equals(date))
                .findAny()
                .orElseThrow(() -> new RuntimeException("타임 테이블 조회 실패"))
                .getTimetables().stream()
                .map(TimetableInfoResponseDto::from)
                .toList();
    }

}
