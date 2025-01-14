package success.planfit.domain.bookmark;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class SpaceBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int user_id;

    @Column(nullable = false)
    private int google_placed_identifier;

    private SpaceBookmark(int user_id, int google_placed_identifier) {
        this.user_id = user_id;
        this.google_placed_identifier = google_placed_identifier;
    }

}
