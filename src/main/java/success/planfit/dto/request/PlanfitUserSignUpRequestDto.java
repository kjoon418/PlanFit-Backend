package success.planfit.dto.request;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import success.planfit.domain.user.IdentityType;
import success.planfit.domain.user.PlanfitUser;
import success.planfit.photo.PhotoProvider;
import success.planfit.photo.PhotoType;

import java.time.LocalDate;

@Getter
public class PlanfitUserSignUpRequestDto {

    private String name;
    private String loginId;
    private String password;
    private String phoneNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthOfDate;
    private IdentityType identity;
    private String email;
    private String profilePhoto;
    private PhotoType photoType;

    public PlanfitUser toEntity() {

        photoType = photoType == null ? PhotoType.NULL : photoType;

        byte[] photo = switch (photoType) {
            case URL -> PhotoProvider.getImageFromUrl(profilePhoto);
            case ENCODED_BINARY -> PhotoProvider.decode(profilePhoto);
            default -> null;
        };

        return PlanfitUser.builder()
                .name(name)
                .loginId(loginId)
                .password(password)
                .phoneNumber(phoneNumber)
                .birthOfDate(birthOfDate)
                .identity(identity)
                .email(email)
                .profilePhoto(photo)
                .build();
    }
}
