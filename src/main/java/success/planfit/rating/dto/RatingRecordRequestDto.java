package success.planfit.rating.dto;

import lombok.Getter;

@Getter
public record RatingRecordRequestDto(
        Integer ratingValue,
        Long courseId
) {
}
