package success.planfit.utils;

import lombok.Builder;

@Builder
public record UserInfo(
        long userId,
        String accessToken,
        String refreshToken
) { }