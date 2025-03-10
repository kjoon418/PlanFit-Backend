package success.planfit.dto.request;


import lombok.Getter;
import success.planfit.domain.course.SpaceType;
import success.planfit.domain.embeddable.SpaceInformation;
import success.planfit.domain.post.SpacePost;
import success.planfit.photo.PhotoProvider;

@Getter
public class SpacePostSaveRequestDto {

    private String title;
    private Integer sequence;
    private String spaceName;
    private String location;
    private SpaceType spaceTag;
    private String link;
    private Double latitude;
    private Double longitude;
    private String spacePhoto;


    public SpacePost toEntity(){
        return SpacePost.builder()
                .sequence(sequence)
                .spaceInformation(SpaceInformation.builder()
                        .spaceName(spaceName)
                        .location(location)
                        .spaceTag(spaceTag)
                        .link(link)
                        .latitude(latitude)
                        .longitude(longitude)
                        .spacePhoto(PhotoProvider.decode(spacePhoto))
                        .build())
                .build();
    }

}
