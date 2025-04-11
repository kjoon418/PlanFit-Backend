package success.planfit.fixture;

import success.planfit.entity.schedule.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;

public enum ScheduleFixture {

    SCHEDULE_A(
            "scheduleA_TITLE",
            LocalDate.of(2024, 12, 25),
            LocalTime.of(11, 30),
            "scheduleA_CONTENT"
    ),
    SCHEDULE_B(
            "scheduleB_TITLE",
            LocalDate.of(2025, 1, 1),
            LocalTime.of(6, 30),
            "scheduleB_CONTENT"
    ),
    SCHEDULE_C(
            "scheduleC_TITLE",
            LocalDate.of(2025, 2, 2),
            LocalTime.of(2, 22),
            "scheduleC_CONTENT"
    ),
    SCHEDULE_D(
            "scheduleB_TITLE",
            LocalDate.of(2025, 3, 3),
            LocalTime.of(3, 33),
            "scheduleD_CONTENT"
    );


    private final String title;
    private final LocalDate date;
    private final LocalTime startTime;
    private final String content;

    ScheduleFixture(
            String title,
            LocalDate date,
            LocalTime startTime,
            String content
    ) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.content = content;
    }

    public Schedule createInstance() {
        return Schedule.builder()
                .title(title)
                .date(date)
                .startTime(startTime)
                .content(content)
                .build();
    }

}
