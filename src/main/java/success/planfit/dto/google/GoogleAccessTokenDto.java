package success.planfit.dto.google;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GoogleAccessTokenDto {

    @SerializedName("access_token")
    private String accessToken;
}
