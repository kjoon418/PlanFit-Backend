package success.planfit.domain.post;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import success.planfit.domain.course.Timetable;
import success.planfit.domain.embeddable.SpaceInformation;

@Getter
@NoArgsConstructor
@Entity
public class SpacePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(nullable = false)
    @ManyToOne (fetch = FetchType.LAZY)
    private CoursePost coursePost;

    @Column(nullable = false)
    private Integer sequence;

    @Setter
    @Embedded
    private SpaceInformation spaceInformation;
    @Column(nullable = false)
    private String spaceName;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    private SpaceType spaceTag;

    @Column(nullable = false)
    private String link;

    private Double latitude;

    private Double longitude;

    private byte[] spacePhoto;

    @Builder
    private SpacePost(CoursePost coursePost, Integer sequence
            , SpaceInformation spaceInformation
    ) {
        this.coursePost = coursePost;
        this.sequence = sequence;
        this.spaceInformation = spaceInformation;
    }

    /**
     * Timetable을 통해 SpacePost 생성하는 정적 팩터리 메서드
     */
    public static SpacePost from(Timetable timetable){
        return SpacePost.builder()
                .sequence(timetable.getSequence())
                .spaceInformation(timetable.getSpaceInformation())
                .build();
    }
}
