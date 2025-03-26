package success.planfit.entity.course;

import jakarta.persistence.*;
import lombok.*;
import success.planfit.entity.space.Space;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String location;

    @OneToMany(mappedBy = "course", fetch = LAZY, orphanRemoval = true, cascade = ALL)
    private final List<Space> spaces = new ArrayList<>();

    @Builder
    private Course(String location) {
        this.location = location;
    }

    public static Course copyOf(Course original) {
        Course copy = Course.builder()
                .location(original.location)
                .build();
        copy.addSpaces(original.spaces);

         return copy;
    }

    /**
     * Course - Space 연관관계 편의 메서드(전체 생성)
     */
    public void addSpaces(List<Space> spaces) {
        for (Space space : spaces) {
            space.setCourse(this);
        }

        this.spaces.addAll(spaces);
    }

    /**
     * Course - Space 연관관계 편의 메서드(전체 삭제)
     */
    public void removeEverySpace() {
        for (Space space : spaces) {
            space.setCourse(null);
        }

        spaces.clear();
    }

    /**
     * Course - Space 연관관계 편의 메서드(생성)
     */
    public void addSpace(Space space) {
        this.spaces.add(space);
        space.setCourse(this);
    }



}
