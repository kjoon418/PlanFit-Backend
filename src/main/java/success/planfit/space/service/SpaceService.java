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
import success.planfit.global.photo.PhotoProvider;
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

    // AI에게 장소 조회 요청
    public SpaceInfoForAIDto requestToAI(Long userId, SpaceDetailRequestDto requestDto){
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 조회 실패"));
        return SpaceInfoForAIDto.toDto(user, requestDto);
    }

    // AI에게 장소 받아서 갱신, 정렬 후, 프론트 장소 리스트에게 전달
    public List<SpaceResponseDto> responseToFE(List<SpaceRequestFromAI> requestDtoList){
        List<SpaceResponseDto> spaceResponseDtoList= new ArrayList<>();
        for (SpaceRequestFromAI spaceRequestFromAI : requestDtoList) {
            SpaceDetail spaceDetail = SpaceDetail.builder()
                    .googlePlacesIdentifier(spaceRequestFromAI.getGooglePlacesIdentifier())
                    .spaceName(spaceRequestFromAI.getName())
                    .location(spaceRequestFromAI.getLocation())
                    .spaceType(spaceRequestFromAI.getSpaceType())
                    .latitude(spaceRequestFromAI.getLatitude())
                    .longitude(spaceRequestFromAI.getLongitude())
                    .link(spaceRequestFromAI.getLink())
                    .build();

            List<byte[]> spacePhotoList = spaceRequestFromAI.getSpacePhotos().stream()
                    .map(PhotoProvider::decode)
                    .toList();

            for (byte[] sp : spacePhotoList) {
                SpacePhoto spacePhoto = SpacePhoto.builder()
                        .spaceDetail(spaceDetail)
                        .value(sp)
                        .build();
                spaceDetail.addSpacePhoto(spacePhoto);
                Space space = Space.builder()
                        .spaceDetail(spaceDetail)
                        .sequence(spaceRequestFromAI.getSimilarityOrder())
                        .build();
            }
        }
        return spaceResponseDtoList;
    }



}
