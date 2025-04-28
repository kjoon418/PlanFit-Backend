package success.planfit.rating.dto;

import lombok.Builder;
import success.planfit.entity.rating.Rating;
import success.planfit.entity.schedule.Schedule;
import success.planfit.entity.space.Space;
import success.planfit.entity.space.SpacePhoto;
import success.planfit.global.photo.PhotoProvider;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Builder(access = PRIVATE)
public record RatingInfoResponseDto(
        long scheduleId,
        String scheduleTitle,
        int ratingValue,
        LocalDateTime date,
        String titlePhoto
) {

    public static RatingInfoResponseDto from(Schedule schedule, Rating rating) {
        return RatingInfoResponseDto.builder()
                .scheduleId(schedule.getId())
                .scheduleTitle(schedule.getTitle())
                .ratingValue(rating.getValue())
                .date(getDate(schedule))
                .titlePhoto(getTitlePhoto(schedule))
                .build();
    }

    private static LocalDateTime getDate(Schedule schedule) {
        return LocalDateTime.of(schedule.getDate(), schedule.getStartTime());
    }

    private static String getTitlePhoto(Schedule schedule) {
        List<SpacePhoto> spacePhotos = schedule.getCourse()
                .getSpaces()
                .stream()
                .min(Comparator.comparingInt(Space::getSequence))
                .orElseThrow(() -> new RuntimeException("데이터 손상 감지: 일정 내 장소 정보를 찾을 수 없습니다."))
                .getSpaceDetail()
                .getSpacePhotos();

        if (spacePhotos.isEmpty()) {
            return null;
        }

        byte[] photoOfFirstSpace = spacePhotos.getFirst()
                .getValue();

        return PhotoProvider.encode(photoOfFirstSpace);
    }

}
