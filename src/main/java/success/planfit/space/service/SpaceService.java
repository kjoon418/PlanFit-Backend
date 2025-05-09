package success.planfit.space.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.course.dto.SpaceDetailInfoDto;
import success.planfit.course.dto.SpaceRequestDto;
import success.planfit.entity.course.Course;
import success.planfit.entity.schedule.Schedule;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.space.SpacePhoto;
import success.planfit.entity.user.User;
import jakarta.persistence.EntityNotFoundException;
import success.planfit.repository.ScheduleRepository;
import success.planfit.repository.SpaceDetailRepository;
import success.planfit.repository.UserRepository;
import success.planfit.space.dto.request.SpaceDetailRequestDto;
import success.planfit.space.dto.response.SpaceResponseFromAI;
import success.planfit.space.dto.request.SpaceInfoForAIDto;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SpaceService {

    private final UserRepository userRepository;
    private final SpaceDetailRepository spaceDetailRepository;
    private final ScheduleRepository scheduleRepository;

    /**
     * AI에게 장소 조회 요청
     */
    public SpaceInfoForAIDto requestToAI(long userId, SpaceDetailRequestDto requestDto){
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));
        return SpaceInfoForAIDto.of(user, requestDto);
    }

    // AI에게 장소 받아서 캐싱, 정렬 후, 프론트 장소 리스트에게 전달
    public List<SpaceDetailInfoDto> responseToFE(List<SpaceResponseFromAI> requestDtos){
        List<SpaceDetailInfoDto> responseDtos = new ArrayList<>();
        for (SpaceResponseFromAI requestDto : requestDtos) {
            SpaceDetail spaceDetail = createSpaceDetail(requestDto);
            List<SpacePhoto> spacePhotos = SpacePhoto.createSpacePhoto(requestDto.getSpacePhotos());
            spaceDetail.addSpacePhotos(spacePhotos);
            spaceDetailRepository.save(spaceDetail);
            responseDtos.add(SpaceDetailInfoDto.of(spaceDetail));
        }
        return responseDtos;
    }

    // 직접 장소 고르기
    public List<SpaceRequestDto> getSpacesFromUser(List<SpaceRequestDto> requestDtos){
        return requestDtos;
    }

    // 스케줄 참조해서 그 안에 있는 코스의 장소리스트
    public List<SpaceRequestDto> getSpacesFromSchedule(long userId, long scheduleId){
        Schedule schedule = scheduleRepository.findIdWithCourseAndSpace(scheduleId)
                .filter(scheduleForFilter -> scheduleForFilter.getUser().getId().equals(userId))
                .orElseThrow(() -> new EntityNotFoundException("스케줄을 찾을 수 없습니다."));

        Course course = schedule.getCourse();
        return course.getSpaces().stream()
                .map(space -> {
                    return SpaceRequestDto.builder()
                            .googlePlacesIdentifier(space.getSpaceDetail().getGooglePlacesIdentifier())
                            .build();
                })
                .toList();
    }

    /**
     * 장소 상세 정보 조회
     */
    @Transactional(readOnly = true)
    public SpaceDetailInfoDto findSpaceDetailInfo(long spaceDetailId){
        return spaceDetailRepository.findById(spaceDetailId).stream()
                .map(SpaceDetailInfoDto::of)
                .findAny()
                .orElseThrow(() ->  new EntityNotFoundException("장소 정보를 찾을 수 없음"));
    }

    private static SpaceDetail createSpaceDetail(SpaceResponseFromAI spaceResponseFromAI) {
        SpaceDetail spaceDetail = SpaceDetail.builder()
                .googlePlacesIdentifier(spaceResponseFromAI.getGooglePlacesIdentifier())
                .spaceName(spaceResponseFromAI.getName())
                .location(spaceResponseFromAI.getLocation())
                .spaceType(spaceResponseFromAI.getSpaceType())
                .latitude(spaceResponseFromAI.getLatitude())
                .longitude(spaceResponseFromAI.getLongitude())
                .link(spaceResponseFromAI.getLink())
                .build();
        return spaceDetail;
    }

}
