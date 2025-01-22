package success.planfit.dto.request;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import success.planfit.domain.user.IdentityType;
import success.planfit.domain.user.PlanfitUser;
import success.planfit.photo.PhotoProvider;

import java.time.LocalDate;

@Getter
public class PlanFitUserSignUpRequestDto {

    private String name;
    private String loginId;
    private String password;
    private String phoneNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthOfDate;
    private IdentityType identity;
    private String email;
    private String profileUrl;

    public PlanfitUser toEntity() {
        return PlanfitUser.builder()
                .name(name)
                .loginId(loginId)
                .password(password)
                .phoneNumber(phoneNumber)
                .birthOfDate(birthOfDate)
                .identity(identity)
                .email(email)
                .profilePhoto(PhotoProvider.getImageFromUrl(profileUrl))
                .build();
    }
}
