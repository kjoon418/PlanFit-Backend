package success.planfit.entity.preference;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import success.planfit.entity.user.User;

@Getter
@NoArgsConstructor
@Entity
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Setter
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Preference preference;

    @Builder
    private UserPreference(User user, Preference preference){
        this.user = user;
        this.preference = preference;
    }
}
