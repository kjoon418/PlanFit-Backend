package success.planfit.schedule.dto;

import lombok.Getter;
import success.planfit.entity.course.SpaceInformation;
import success.planfit.entity.space.SpaceType;
import success.planfit.entity.course.Timetable;
import success.planfit.global.photo.PhotoProvider;


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
