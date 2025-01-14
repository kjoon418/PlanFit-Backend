package success.planfit.domain.user;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class PlanfitUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int user_id;

    @Column(nullable = false)
    private int login_id;

    @Column(nullable = false)
    private int password;

    @Builder
    private PlanfitUser(int user_id, int login_id, int password) {
        this.user_id = user_id;
        this.login_id = login_id;
        this.password = password;
    }

}
