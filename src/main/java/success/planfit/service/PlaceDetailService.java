package success.planfit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import success.planfit.domain.bookmark.SpaceBookmark;
import success.planfit.dto.request.PlaceDetailRequestDto;
import success.planfit.dto.response.PlaceDetailResponseDto;

import java.util.List;

@AllArgsConstructor
@Service
public class PlaceDetailService {
    private OpenGooglePlaceApi openGooglePlaceApi;

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


    public List<PlaceDetailResponseDto> getPlaceDetailsByLocation(PlaceDetailRequestDto requestDto){
        String placeDetailJson = openGooglePlaceApi.fetchPlaceDetailsByLocation(requestDto);

        // json string -> list<dto>
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<PlaceDetailResponseDto> placelist;

        try {
            placelist = objectMapper.readValue(placeDetailJson,
                    new TypeReference<List<PlaceDetailResponseDto>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return placelist;
    }




}
