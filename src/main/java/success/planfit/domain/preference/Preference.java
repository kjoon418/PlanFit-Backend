package success.planfit.domain.preference;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Entity
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(unique = true)
    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private PreferenceType name;

    @Builder
    public Preference(PreferenceType name) {
        this.name = name;
    }
}
