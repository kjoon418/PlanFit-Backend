package success.planfit.domain.user;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    private String phoneNumber;

    @Setter
    private LocalDate birthOfDate;

    @Setter
    @Enumerated(EnumType.STRING)
    private IdentityType identity;

    @Setter
    @Column(nullable = false)
    private String email;

    @Setter
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

    public void addCalendar(Calendar calendar){
        calendars.add(calendar);
        calendar.setUser(this);
    }

    public void removeCalendar(Calendar calendar){
        calendars.remove(calendar);
        calendar.setUser(null);
    }

    /**
     * User - CourseBookmark 연관관계 편의 메서드(생성)
     */
    public void addCourseBookmark(CourseBookmark courseBookmark) {
        this.courseBookmarks.add(courseBookmark);
        courseBookmark.setUser(this);
    }

    /**
     * User - CourseBookmark 연관관계 편의 메서드(삭제)
     */
    public void removeCourseBookmark(CourseBookmark courseBookmark) {
        this.courseBookmarks.remove(courseBookmark);
        courseBookmark.setUser(null);
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

    /**
     * User - Calendar 연관관계 편의 메서드(생성)
     */
    public void addCalendar(Calendar calendar) {
        this.calendars.add(calendar);
        calendar.setUser(this);
    }

    /**
     * User - Calendar 연관관계 편의 메서드(삭제)
     */
    public void removeCalendar(Calendar calendar) {
        this.calendars.remove(calendar);
        calendar.setUser(null);
    }

    /**
     * User - UserPreference 연관관계 편의 메서드(생성)
     */
    public void addUserPreference(UserPreference userPreference) {
        this.userPreferences.add(userPreference);
        userPreference.setUser(this);
    }

    /**
     * User - UserPreference 연관관계 편의 메서드(삭제)
     */
    public void removeUserPreference(UserPreference userPreference) {
        this.userPreferences.remove(userPreference);
        userPreference.setUser(null);
    }

}
