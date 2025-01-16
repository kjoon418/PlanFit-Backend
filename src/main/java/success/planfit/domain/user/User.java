package success.planfit.domain.user;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import success.planfit.domain.RefreshToken;

import java.time.LocalDate;


@Getter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="authorized_by", discriminatorType = DiscriminatorType.STRING)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    @Setter
    private RefreshToken refreshToken;

    @Column(nullable = false)
    private String name;

    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate birthOfDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdentityType identity;

    @Column(nullable = false)
    private String email;

    private String profileUrl;

    protected User(String name, String phoneNumber, LocalDate birthOfDate, IdentityType identity, String email, String profileUrl, RefreshToken refreshToken){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthOfDate = birthOfDate;
        this.identity = identity;
        this.email = email;
        this.profileUrl = profileUrl;
        this.refreshToken = refreshToken;
    }

}
