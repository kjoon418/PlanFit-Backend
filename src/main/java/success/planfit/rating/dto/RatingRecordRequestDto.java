package success.planfit.rating.dto;

public record RatingRecordRequestDto(
        String googlePlacesIdentifier,
        int rating
) {
}
