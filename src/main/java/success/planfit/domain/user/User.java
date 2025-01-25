package success.planfit.domain.user;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import success.planfit.domain.RefreshToken;
import success.planfit.domain.bookmark.CourseBookmark;
import success.planfit.domain.bookmark.SpaceBookmark;
import success.planfit.domain.course.Calendar;
import success.planfit.domain.preference.UserPreference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private final List<CourseBookmark> courseBookmarks = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private final List<SpaceBookmark> spaceBookmarks = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private final List<Calendar> calendars = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private final List<UserPreference> userPreferences = new ArrayList<>();

    @Column(nullable = false)
    private String name;

    private String phoneNumber;

    private LocalDate birthOfDate;

    @Enumerated(EnumType.STRING)
    private IdentityType identity;

    @Column(nullable = false)
    private String email;

    @Lob
    private byte[] profilePhoto;

    protected User(String name, String phoneNumber, LocalDate birthOfDate, IdentityType identity, String email, byte[] profilePhoto){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthOfDate = birthOfDate;
        this.identity = identity;
        this.email = email;
        this.profilePhoto = profilePhoto;
        this.refreshToken = RefreshToken.builder().build(); // 빈 값인 RefreshToken 엔티티 생성
    }

    /**
     * User - SpaceBookmark 연관관계 편의 메서드(생성)
     */
    public void addSpaceBookmark(SpaceBookmark spaceBookmark) {
        this.spaceBookmarks.add(spaceBookmark);
        spaceBookmark.setUser(this);
    }

    /**
     * User - SpaceBookmark 연관관계 편의 메서드(삭제)
     */
    public void removeSpaceBookmark(SpaceBookmark spaceBookmark) {
        this.spaceBookmarks.remove(spaceBookmark);
        spaceBookmark.setUser(null);
    }
}
