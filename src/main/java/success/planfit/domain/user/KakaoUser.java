package success.planfit.domain.user;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class KakaoUser extends User {

    @Column(nullable = false, unique = true)
    private Long kakaoIdentifier;

    @Builder
    private KakaoUser(String name, String phoneNumber, LocalDate birthOfDate, IdentityType identity, String email, byte[] profilePhoto, Long kakaoIdentifier) {
        super(name, phoneNumber, birthOfDate, identity, email, profilePhoto);
        this.kakaoIdentifier = kakaoIdentifier;
    }
}
