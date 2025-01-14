package success.planfit.domain.bookmark;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class CourseBookmark {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    private int user_id;

    @Column(nullable = false)
    private int title;

    @Column(nullable = false)
    private int date;

    @Column(nullable = false)
    private int title_photo;

}
