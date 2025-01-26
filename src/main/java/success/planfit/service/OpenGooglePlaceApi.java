package success.planfit.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import success.planfit.dto.request.PlaceDetailRequestDto;

@Component
public class OpenGooglePlaceApi {
    private final RestTemplate restTemplate;
    private final String apiKey;

    public OpenGooglePlaceApi(RestTemplateBuilder restTemplateBuilder, @Value("${GOOGLE_PLACES_API_KEY}") String apiKey) {
        this.restTemplate = restTemplateBuilder.build();
        this.apiKey = apiKey;
    }

    public String fetchPlaceDetailsByplaceId(String placeId){
        String fields = null;
        String url = String.format( "https://places.googleapis.com/v1/places/%s?fields=%s&key=%s",
                placeId, fields, apiKey);

        try {
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e){
            throw new RuntimeException("Failed to fetch place details: " + e.getMessage(), e);
        }
    }

    public String fetchPlaceDetailsByLocation(PlaceDetailRequestDto requestDto){
        // 엔드포인트 URL
        String url = "https://places.googleapis.com/v1/places:searchNearby?fields=places.types&key=" + apiKey;

        // 요청 본문 생성 (JSON 형식)
        String requestBody = String.format(
                "{\"locationRestriction\": {\"circle\": {\"center\": {\"latitude\": %f,\"longitude\": %f}, \"radius\": %d}}}",
                requestDto.getLatitude(), requestDto.getLongitude(), requestDto.getRadius()
        );

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity 객체로 요청 본문과 헤더 결합
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // REST POST 요청
            String jsonString = restTemplate.postForObject(url, requestEntity, String.class);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject contents = jsonObject.getJSONObject("places");
            return contents.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch place details: " + e.getMessage(), e);
        }
    }
}
