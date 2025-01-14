package success.planfit.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Entity
public class GoogleUser {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    private int user_id;

    @Column(nullable = false)
    private String google_identifier;

    @Builder
    private GoogleUser(int user_id, String google_identifier) {
        this.user_id = user_id;
        this.google_identifier = google_identifier;
    }
}
