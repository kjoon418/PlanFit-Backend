package success.planfit.domain.user;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class KakaoUser {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    private int user_id;

    @Column(nullable = false)
    private int kakao_identifier;

    @Builder
    private KakaoUser(int user_id, int kakao_identifier) {
        this.user_id = user_id;
        this.kakao_identifier = kakao_identifier;
    }
}
