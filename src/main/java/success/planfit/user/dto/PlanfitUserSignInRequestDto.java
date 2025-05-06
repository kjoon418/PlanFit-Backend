package success.planfit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import success.planfit.global.validation.NotEmptyAndNotBlank;

@Getter
@Builder
@AllArgsConstructor
public class PlanfitUserSignInRequestDto {

    @NotEmptyAndNotBlank(value = "loginId", allowWhiteSpace = false)
    private String loginId;

    @NotEmptyAndNotBlank(value = "password", allowWhiteSpace = false)
    private String password;

}
