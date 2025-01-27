package success.planfit.dto.request;

import lombok.Getter;
import success.planfit.domain.course.SpaceType;
import success.planfit.domain.course.Timetable;
import success.planfit.domain.embeddable.SpaceInformation;
import success.planfit.photo.PhotoProvider;

@Getter
public class TimetableCreationRequestDto {

    private Integer sequence;
    private String memo;
    private String spaceName;
    private String location;
    private String link;
    private Double latitude;
    private Double longitude;
    private String spacePhoto;
    private SpaceType spaceType;

    public Timetable toEntity() {
        return Timetable.builder()
                .sequence(sequence)
                .memo(memo)
                .spaceInformation(SpaceInformation.builder().spaceName(spaceName)
                        .location(location)
                        .link(link)
                        .latitude(latitude)
                        .longitude(longitude)
                        .spacePhoto(PhotoProvider.decode(spacePhoto))
                        .spaceTag(spaceType).build())
                .build();
    }

}
