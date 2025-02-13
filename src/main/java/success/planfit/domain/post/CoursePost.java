package success.planfit.domain.post;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import success.planfit.domain.user.User;

@Getter
@NoArgsConstructor
@Entity
public class CoursePost {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    private String location;

    private byte[] titlePhoto;

    @Builder
    private CoursePost(User user, String title, String location, byte[] titlePhoto) {
        this.user = user;
        this.title = title;
        this.location = location;
        this.titlePhoto = titlePhoto;
    }

}
