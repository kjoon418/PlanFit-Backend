package success.planfit.dto.request;

import lombok.Getter;
import success.planfit.validation.NotEmptyAndNotBlank;

@Getter
public class PlanfitUserSignInRequestDto {

    @NotEmptyAndNotBlank(value = "loginId", allowWhiteSpace = false)
    private String loginId;

    @NotEmptyAndNotBlank(value = "password", allowWhiteSpace = false)
    private String password;

}
