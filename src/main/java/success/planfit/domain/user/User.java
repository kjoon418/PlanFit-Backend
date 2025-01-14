package success.planfit.domain.user;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@Entity
@DiscriminatorColumn(name="authorized_by", discriminatorType = DiscriminatorType.STRING)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    private String phone_number;

    @Column(nullable = false)
    private LocalDateTime birth_of_date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdentityType identity;

    @Column(nullable = false)
    private String email;

    private String profile_url;

    @Builder
    private User(String name, String phone_number, LocalDateTime birth_of_date, IdentityType identity, String email
    , String profile_url){
        this.name = name;
        this.phone_number = phone_number;
        this.birth_of_date = birth_of_date;
        this.identity = identity;
        this.email = email;
        this.profile_url = profile_url;
    }



}
