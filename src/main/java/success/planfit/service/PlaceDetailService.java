package success.planfit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import success.planfit.domain.bookmark.SpaceBookmark;
import success.planfit.domain.user.UserDto;
import success.planfit.dto.request.PlaceDetailRequestDto;
import success.planfit.dto.response.LocationDetailResponseDto;
import success.planfit.dto.response.PlaceDetailResponseDto;
import success.planfit.repository.UserRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class PlaceDetailService {
    private OpenGooglePlaceApi openGooglePlaceApi;
    private UserRepository userRepository;

    // AI에게 좌표값과 유저 정보 전달
    // 원래는 프론트한테 좌표값받아서 주변장소정보를 전달하는 줄 알앗음
    public LocationDetailResponseDto passPlaceDetail(Long id, PlaceDetailRequestDto placeDetailRequestDto){
        return LocationDetailResponseDto.builder()
                .userDto(UserDto.from(userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("ID를 통해 유저 조회 실패"))))
                .placeDetailRequestDto(placeDetailRequestDto)
                .build();
    }


    // AI에게 받은 값들로 API 조회후 정보값 전달
    public PlaceDetailResponseDto getPlaceDetailsById(String placeId){
        String placeDetailJson = openGooglePlaceApi.fetchPlaceDetailsByplaceId(placeId);

        // json String SpaceBook 객체에 넣기
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PlaceDetailResponseDto responseDto;

        try {
            responseDto = objectMapper.readValue(placeDetailJson,
                    new TypeReference<PlaceDetailResponseDto>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // 구현: googleApi로 가져온 디테일 테이블에 저장
        return responseDto;
    }



}
