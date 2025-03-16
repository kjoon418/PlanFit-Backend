package success.planfit.global.jwt.dto;

import lombok.Builder;

@Builder
public record AccessTokenResponseDto(String accessToken) {

}
