package success.planfit.domain.user;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class PlanfitUser extends User {

    @Column(nullable = false, unique = true)
    private String loginId;

    @Setter
    @Column(nullable = false)
    private String password;

    @Builder
    private PlanfitUser(String name, String phoneNumber, LocalDate birthOfDate, IdentityType identity, String email, byte[] profilePhoto, String loginId, String password) {
        super(name, phoneNumber, birthOfDate, identity, email, profilePhoto);
        this.loginId = loginId;
        this.password = password;
    }

}
