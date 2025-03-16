package success.planfit.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import success.planfit.entity.space.SpaceType;
import success.planfit.entity.course.Timetable;
import success.planfit.global.photo.PhotoProvider;

@Getter
@AllArgsConstructor
@Builder
public class TimetableInfoResponseDto {
    private Long timetableId;
    private Integer sequence;
    private String memo;
    private String spaceName;
    private String location;
    private String link;
    private Double latitude;
    private Double longitude;
    private String spacePhoto;
    private SpaceType spaceType;

    public static TimetableInfoResponseDto from(Timetable timetable) {
        return TimetableInfoResponseDto.builder()
                .memo(timetable.getMemo())
                .timetableId(timetable.getId())
                .sequence(timetable.getSequence())
                .spaceName(timetable.getSpaceInformation().getSpaceName())
                .location(timetable.getSpaceInformation().getLocation())
                .spaceType(timetable.getSpaceInformation().getSpaceTag())
                .link(timetable.getSpaceInformation().getLink())
                .latitude(timetable.getSpaceInformation().getLatitude())
                .longitude(timetable.getSpaceInformation().getLongitude())
                .spacePhoto(PhotoProvider.encode(timetable.getSpaceInformation().getSpacePhoto()))
                .build();
    }
}
