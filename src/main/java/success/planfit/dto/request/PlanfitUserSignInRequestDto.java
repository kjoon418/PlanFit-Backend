package success.planfit.dto.request;

import lombok.Getter;
import success.planfit.validation.NotEmptyAndNotBlank;

@Getter
public class PlanfitUserSignInRequestDto {

    @NotEmptyAndNotBlank(value = "loginId")
    private String loginId;

    @NotEmptyAndNotBlank(value = "password")
    private String password;

}
