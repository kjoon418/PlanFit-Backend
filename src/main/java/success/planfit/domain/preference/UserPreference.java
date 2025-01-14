package success.planfit.domain.preference;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int user_id;

    @Column(nullable = false)
    private int preference_id;

    @Builder
    private UserPreference(int id, int user_id, int preference_id) {
        this.id = id;
        this.user_id = user_id;
        this.preference_id = preference_id;
    }
}
