package success.planfit.space.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import success.planfit.course.dto.SpaceDetailInfoDto;
import success.planfit.course.dto.SpaceRequestDto;
import success.planfit.entity.course.Course;
import success.planfit.entity.schedule.Schedule;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.space.SpacePhoto;
import success.planfit.repository.ScheduleRepository;
import success.planfit.repository.SpaceDetailRepository;
import success.planfit.space.dto.request.SpaceDetailRequestDto;
import success.planfit.space.dto.request.SpaceInfoForAIDto;

import java.util.List;
import java.util.Optional;


@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SpaceService {

    private final SpaceDetailRepository spaceDetailRepository;
    private final ScheduleRepository scheduleRepository;
    private final String URL = "";

    /**
     * AI에게 장소 조회 요청
     */
    public List<SpaceDetailInfoDto> getSpaceDetails(SpaceDetailRequestDto requestDto){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<SpaceDetailInfoDto>> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(SpaceInfoForAIDto.of(requestDto)), new ParameterizedTypeReference<>() {
        });

        if (isRequestSuccess(responseEntity)){
            List<SpaceDetailInfoDto> spaceDetailInfoDtos = responseEntity.getBody();
            saveSpaceDetail(spaceDetailInfoDtos);

            return spaceDetailInfoDtos.stream().
                    sorted().toList();
        }
        throw new RuntimeException("장소 조회 실패");
    }

    /**
     * 직접 장소 고르기
     */
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
                .map(space -> SpaceRequestDto.builder()
                            .googlePlacesIdentifier(space.getSpaceDetail().getGooglePlacesIdentifier())
                            .build()
                )
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

    private static SpaceDetail createSpaceDetail(SpaceDetailInfoDto spaceDetailInfoDto) {
        SpaceDetail spaceDetail = SpaceDetail.builder()
                .googlePlacesIdentifier(spaceDetailInfoDto.getGooglePlacesIdentifier())
                .spaceName(spaceDetailInfoDto.getSpaceName())
                .location(spaceDetailInfoDto.getLocation())
                .spaceType(spaceDetailInfoDto.getSpaceType())
                .latitude(spaceDetailInfoDto.getLatitude())
                .longitude(spaceDetailInfoDto.getLongitude())
                .link(spaceDetailInfoDto.getLink())
                .build();
        return spaceDetail;
    }

    private boolean isRequestSuccess(ResponseEntity<List<SpaceDetailInfoDto>> responseEntity) {
        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    private void saveSpaceDetail(List<SpaceDetailInfoDto> SpaceDetailInfoDtos) {
        SpaceDetailInfoDtos.stream()
                .map(spaceDetailInfoDto -> {
                    Optional<SpaceDetail> foundSpaceDetail = spaceDetailRepository.findByGooglePlacesIdentifier(spaceDetailInfoDto.getGooglePlacesIdentifier());
                    // 만약에 DB 값이 있다면 데이터 업데이트
                    foundSpaceDetail.ifPresent(spaceDetail ->
                        spaceDetail.update(spaceDetailInfoDto)
                    );
                    // 만약에 DB 값이 없다면 DB에 저장
                    SpaceDetail spaceDetail = createSpaceDetail(spaceDetailInfoDto);
                    List<SpacePhoto> spacePhotos = SpacePhoto.createSpacePhoto(spaceDetailInfoDto.getSpacePhotos());
                    spaceDetail.addSpacePhotos(spacePhotos);
                    spaceDetailRepository.save(spaceDetail);

                    return SpaceDetailInfoDto.of(spaceDetail);
                });
    }

}
