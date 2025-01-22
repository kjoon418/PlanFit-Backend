package success.planfit.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Getter
@NoArgsConstructor
@Entity
public class GoogleUser extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String googleIdentifier;

    @Builder
    private GoogleUser(String name, String phoneNumber, LocalDate birthOfDate, IdentityType identity, String email, byte[] profilePhoto, String googleIdentifier) {
        super(name, phoneNumber, birthOfDate, identity, email, profilePhoto);
        this.googleIdentifier = googleIdentifier;
    }
}
