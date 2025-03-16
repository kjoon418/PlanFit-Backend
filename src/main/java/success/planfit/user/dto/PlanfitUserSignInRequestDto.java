package success.planfit.user.dto;

import lombok.Getter;
import success.planfit.global.validation.NotEmptyAndNotBlank;

@Getter
public class PlanfitUserSignInRequestDto {

    @NotEmptyAndNotBlank(value = "loginId", allowWhiteSpace = false)
    private String loginId;

    @NotEmptyAndNotBlank(value = "password", allowWhiteSpace = false)
    private String password;

}
