package success.planfit.domain.user;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="authorized_by", discriminatorType = DiscriminatorType.STRING)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime birthOfDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdentityType identity;

    @Column(nullable = false)
    private String email;

    private String profileUrl;

    protected User(String name, String phoneNumber, LocalDateTime birthOfDate, IdentityType identity, String email, String profileUrl){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthOfDate = birthOfDate;
        this.identity = identity;
        this.email = email;
        this.profileUrl = profileUrl;
    }

}
