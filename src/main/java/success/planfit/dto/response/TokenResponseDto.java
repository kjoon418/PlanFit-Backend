package success.planfit.dto.response;

import lombok.Builder;

@Builder
public record TokenResponseDto(String accessToken, String refreshToken) {

}
