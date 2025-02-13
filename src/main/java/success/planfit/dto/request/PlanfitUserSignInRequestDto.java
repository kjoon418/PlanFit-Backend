package success.planfit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class PlanfitUserSignInRequestDto {

    private String loginId;
    private String password;

}
