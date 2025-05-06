package success.planfit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import success.planfit.entity.user.IdentityType;
import success.planfit.entity.user.PlanfitUser;
import success.planfit.global.photo.PhotoProvider;
import success.planfit.global.photo.PhotoType;
import success.planfit.global.validation.NotEmptyAndNotBlank;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class PlanfitUserSignUpRequestDto {

    @NotEmptyAndNotBlank(value = "name")
    private String name;

    @NotEmptyAndNotBlank(value = "loginId", allowWhiteSpace = false)
    private String loginId;

    @NotEmptyAndNotBlank(value = "password", allowWhiteSpace = false)
    private String password;

    @NotEmptyAndNotBlank(value = "email", allowWhiteSpace = false)
    private String email;

    private String phoneNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthOfDate;

    private IdentityType identity;

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
                .email(email)
                .phoneNumber(phoneNumber)
                .birthOfDate(birthOfDate)
                .identity(identity)
                .profilePhoto(photo)
                .build();
    }

}
