package success.planfit.domain.post;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import success.planfit.domain.course.SpaceType;

@Getter
@NoArgsConstructor
@Entity
public class SpacePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne (fetch = FetchType.LAZY)
    private CoursePost coursePost;

    @Column(nullable = false)
    private Integer sequence;

    @Column(nullable = false)
    private String spaceName;

    @Column(nullable = false)
    private String location;

    private SpaceType spaceTag;

    @Column(nullable = false)
    private String link;

    private Double latitude;

    private Double longitude;

    private byte[] spacePhoto;

    @Builder
    private SpacePost(CoursePost coursePost, Integer sequence
            , String spaceName, String location, SpaceType spaceTag
            , String link, Double latitude, Double longitude
                      ,byte[] spacePhoto
    ) {
        this.coursePost = coursePost;
        this.sequence = sequence;
        this.spaceName = spaceName;
        this.location = location;
        this.spaceTag = spaceTag;
        this.link = link;
        this.latitude = latitude;
        this.longitude = longitude;
        this.spacePhoto = spacePhoto;
    }
}
