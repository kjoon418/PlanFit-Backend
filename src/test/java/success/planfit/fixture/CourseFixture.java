package success.planfit.fixture;

import success.planfit.entity.course.Course;

public enum CourseFixture {

    BASIC("BASIC_LOCATION");

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
