package success.planfit.fixture;

import success.planfit.entity.schedule.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;

public enum ScheduleFixture {

    SCHEDULE_A(
            "scheduleA_TITLE",
            LocalDate.of(2024, 12, 25),
            LocalTime.of(11, 30)
    ),
    SCHEDULE_B(
            "scheduleB_TITLE",
            LocalDate.of(2025, 1, 1),
            LocalTime.of(6, 30)
    );

    private final String title;
    private final LocalDate date;
    private final LocalTime startTime;

    ScheduleFixture(
            String title,
            LocalDate date,
            LocalTime startTime
    ) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
    }

    public Schedule createInstance() {
        return Schedule.builder()
                .title(title)
                .date(date)
                .startTime(startTime)
                .build();
    }

}
