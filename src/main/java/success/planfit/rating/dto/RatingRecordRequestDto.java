package success.planfit.rating.dto;

public record RatingRecordRequestDto(
        Integer ratingValue,
        Long courseId
) {
}
