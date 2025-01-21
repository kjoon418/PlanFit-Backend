package success.planfit.domain.preference;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
@Entity
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private PreferenceType name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "preference")
    private final List<UserPreference> userPreferences = new ArrayList<>();

    @Builder
    private Preference(PreferenceType name) {
        this.name = name;
    }
}
