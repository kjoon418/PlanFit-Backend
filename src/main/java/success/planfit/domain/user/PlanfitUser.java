package success.planfit.domain.user;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class PlanfitUser{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false,unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Builder
    private PlanfitUser(User user, String loginId, String password) {
        this.user = user;
        this.loginId = loginId;
        this.password = password;
    }

}
