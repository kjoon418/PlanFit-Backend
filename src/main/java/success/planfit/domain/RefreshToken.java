package success.planfit.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import success.planfit.domain.user.User;

@Getter
@NoArgsConstructor
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn (nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String tokenValue;

    @Builder
    private RefreshToken(User user, String tokenValue) {
        this.user = user;
        this.tokenValue = tokenValue;
    }
}
