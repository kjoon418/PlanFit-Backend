package success.planfit.domain.bookmark;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class TimetableBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int course_bookmark_id;

    @Column(nullable = false)
    private int start_time;

    @Column(nullable = false)
    private int end_time;

    @Column(nullable = false)
    private int space_name;

    @Column(nullable = false)
    private int location;

    private int space_tag;

    private int memo;

    @Column(nullable = false)
    private int link;


}
