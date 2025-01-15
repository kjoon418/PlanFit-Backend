package success.planfit.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


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
    private GoogleUser(String name, String phoneNumber, LocalDateTime birthOfDate, IdentityType identity, String email, String profileUrl, String googleIdentifier) {
        super(name, phoneNumber, birthOfDate, identity, email, profileUrl);
        this.googleIdentifier = googleIdentifier;
    }
}
