package success.planfit.photo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PhotoProvider {

    /**
     * 이미지에 대한 바이너리 데이터를 Base64 인코딩을 통해 문자열로 변환하는 메서드
     */
    public static String encode(byte[] image) {
        return Base64.getEncoder().encodeToString(image);
    }

    /**
     * 이미지에 대한 Base64 문자열 데이터를 바이너리 데이터로 변환하는 메서드
     */
    public static byte[] decode(String image) {
        return Base64.getDecoder().decode(image);
    }

    /**
     * 바이너리 데이터에 대한 문자열을 반환하는 메서드
     */
    public static String getStringFromByte(byte[] byteData) {
        return new String(byteData);
    }

    /**
     * 이미지 URL을 통해 바이너리 데이터를 받아오는 메서드
     */
    public static byte[] getImageFromUrl(String imageUrl) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(imageUrl, byte[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        throw new RuntimeException("이미지 URL을 통한 데이터 조회 실패: " + response.getStatusCode());
    }
}
