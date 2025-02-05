package success.planfit.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import success.planfit.domain.user.IdentityType;
import success.planfit.domain.user.User;
import success.planfit.photo.PhotoProvider;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class UserUpdateDto {
    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate birthOfDate;
    private IdentityType identity;
    private String password;
    private String profilePhoto;

    public static UserUpdateDto from(User user) {
        return UserUpdateDto.builder()
                .name(user.getName())
                .profilePhoto(PhotoProvider.encode(user.getProfilePhoto()))
                .birthOfDate(user.getBirthOfDate())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .identity(user.getIdentity())
                .email(user.getEmail())
                .build();
    }


}
