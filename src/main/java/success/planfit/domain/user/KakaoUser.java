package success.planfit.domain.user;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class KakaoUser extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long kakaoIdentifier;

    @Builder
    private KakaoUser(String name, String phoneNumber, LocalDate birthOfDate, IdentityType identity, String email, String profileUrl, Long kakaoIdentifier) {
        super(name, phoneNumber, birthOfDate, identity, email, profileUrl);
        this.kakaoIdentifier = kakaoIdentifier;
    }
}
