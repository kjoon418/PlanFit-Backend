package success.planfit.global.jwt.dto;

import lombok.Builder;

@Builder
public record TokenResponseDto(String accessToken, String refreshToken) {

}
