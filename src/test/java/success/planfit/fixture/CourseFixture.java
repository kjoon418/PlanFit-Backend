package success.planfit.fixture;

import success.planfit.entity.course.Course;

public enum CourseFixture {

    DEFAULT_COURSE("defaultCourse_LOCATION"),
    COURSE_A("courseA_LOCATION"),
    COURSE_B("courseB_LOCATION"),
    COURSE_C("courseC_LOCATION");

    private final String location;

    CourseFixture(String location) {
        this.location = location;
    }

    public Course createInstance() {
        return Course.builder()
                .location(location)
                .build();
    }

}
