package success.planfit.domain.preference;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import success.planfit.domain.user.User;

@Getter
@NoArgsConstructor
@Entity
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private int preferenceId;

    @Builder
    private UserPreference(User user, int preferenceId){
        this.user = user;
        this.preferenceId = preferenceId;
    }
}
