package success.planfit.user.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 참고 문서: <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info">...</a>
 */
@Getter
@AllArgsConstructor
public class KaKaoUserInfoDto {

    private Long id;

    @SerializedName("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @AllArgsConstructor
    static class KakaoAccount {

        @SerializedName("profile_nickname_needs_agreement")
        private Boolean profileNicknameNeedsAgreement;
        @SerializedName("profile_image_needs_agreement")
        private Boolean profileImageNeedsAgreement;
        private Profile profile;

        @SerializedName("email_needs_agreement")
        private Boolean emailNeedsAgreement;
        @SerializedName("is_email_valid")
        private Boolean isEmailValid;
        @SerializedName("is_email_verified")
        private Boolean getIsEmailVerified;
        private String email;
    }

    @Getter
    @AllArgsConstructor
    static class Profile {

        @SerializedName("nickname")
        private String name;
        @SerializedName("thumbnail_image_url")
        private String thumbnailImageUrl;
        @SerializedName("profile_image_url")
        private String profileImageUrl;
        @SerializedName("is_default_image")
        private Boolean isDefaultImage;
        @SerializedName("is_default_nickname")
        private Boolean isDefaultNickname;
    }

    public String getEmail() {
        return this.kakaoAccount.email;
    }

    public String getName() {
        return this.kakaoAccount.profile.name;
    }

    public String getProfileUrl() {
        return this.kakaoAccount.profile.profileImageUrl;
    }

    public Boolean isValidatedEmail() {
        if (kakaoAccount.isEmailValid == null || kakaoAccount.getIsEmailVerified == null) {
            return false;
        }

        return kakaoAccount.isEmailValid && kakaoAccount.getIsEmailVerified;
    }
}
