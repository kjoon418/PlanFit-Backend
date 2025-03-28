package success.planfit.space.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.course.dto.SpaceResponseDto;
import success.planfit.entity.space.Space;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.space.SpacePhoto;
import success.planfit.entity.user.User;
import success.planfit.global.exception.EntityNotFoundException;
import success.planfit.global.photo.PhotoProvider;
import success.planfit.repository.SpaceDetailRepository;
import success.planfit.repository.UserRepository;
import success.planfit.space.dto.request.SpaceDetailRequestDto;
import success.planfit.space.dto.request.SpaceRequestFromAI;
import success.planfit.space.dto.response.SpaceInfoForAIDto;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SpaceService {

    private final UserRepository userRepository;
    private final SpaceDetailRepository spaceDetailRepository;

    // AI에게 장소 조회 요청
    public SpaceInfoForAIDto requestToAI(Long userId, SpaceDetailRequestDto requestDto){
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));
        return SpaceInfoForAIDto.of(user, requestDto);
    }

    // AI에게 장소 받아서 캐싱, 정렬 후, 프론트 장소 리스트에게 전달
    public List<SpaceResponseDto> responseToFE(List<SpaceRequestFromAI> requestDtos){
        List<Space> spaces= new ArrayList<>();
        List<SpaceResponseDto> spaceResponseDtos = new ArrayList<>();
        for (SpaceRequestFromAI spaceRequestFromAI : requestDtos) {
            SpaceDetail spaceDetail = SpaceDetail.builder()
                    .googlePlacesIdentifier(spaceRequestFromAI.getGooglePlacesIdentifier())
                    .spaceName(spaceRequestFromAI.getName())
                    .location(spaceRequestFromAI.getLocation())
                    .spaceType(spaceRequestFromAI.getSpaceType())
                    .latitude(spaceRequestFromAI.getLatitude())
                    .longitude(spaceRequestFromAI.getLongitude())
                    .link(spaceRequestFromAI.getLink())
                    .build();

            List<byte[]> spacePhotos = spaceRequestFromAI.getSpacePhotos().stream()
                    .map(PhotoProvider::decode)
                    .toList();

            for (byte[] sp : spacePhotos) {
                SpacePhoto spacePhoto = SpacePhoto.builder()
                        .spaceDetail(spaceDetail)
                        .value(sp)
                        .build();
                spaceDetail.addSpacePhoto(spacePhoto);
                Space space = Space.builder()
                        .spaceDetail(spaceDetail)
                        .sequence(spaceRequestFromAI.getSimilarityOrder())
                        .build();
                spaces.add(space);
            }
            // SpaceDetail 테이블에 캐싱
            spaceDetailRepository.save(spaceDetail);
            spaceResponseDtos = spaces.stream()
                    .map(SpaceResponseDto::from)
                    .toList();
        }
        return spaceResponseDtos;
    }



}
