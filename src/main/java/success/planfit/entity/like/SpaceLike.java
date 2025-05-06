package success.planfit.entity.like;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import success.planfit.entity.space.SpaceDetail;
import success.planfit.entity.user.User;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class SpaceLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private SpaceDetail spaceDetail;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder
    private SpaceLike(SpaceDetail spaceDetail, User user){
        this.spaceDetail = spaceDetail;
        this.user = user;
    }

}
