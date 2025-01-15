package success.planfit.domain.user;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class PlanfitUser extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Builder
    private PlanfitUser(String name, String phoneNumber, LocalDateTime birthOfDate, IdentityType identity, String email, String profileUrl, String loginId, String password) {
        super(name, phoneNumber, birthOfDate, identity, email, profileUrl);
        this.loginId = loginId;
        this.password = password;
    }

}
