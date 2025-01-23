package success.planfit.domain.user;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import success.planfit.domain.RefreshToken;

import java.time.LocalDate;


@Getter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    private RefreshToken refreshToken;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    private String phoneNumber;

    @Setter
    private LocalDate birthOfDate;

    @Setter
    @Enumerated(EnumType.STRING)
    private IdentityType identity;

    @Setter
    @Column(nullable = false)
    private String email;

    @Setter
    private String profilePhoto;


    protected User(String name, String phoneNumber, LocalDate birthOfDate, IdentityType identity, String email, String profilePhoto){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthOfDate = birthOfDate;
        this.identity = identity;
        this.email = email;
        this.profilePhoto = profilePhoto;
        this.refreshToken = RefreshToken.builder().build(); // 빈 값인 RefreshToken 엔티티 생성
    }


}
