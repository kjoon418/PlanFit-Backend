package success.planfit.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.domain.bookmark.SpaceBookmark;
import success.planfit.domain.course.SpaceType;
import success.planfit.domain.embeddable.SpaceInformation;
import success.planfit.domain.user.User;
import success.planfit.dto.request.SpaceBookmarkRegistrationRequestDto;
import success.planfit.dto.response.SpaceBookmarkInfoResponseDto;
import success.planfit.photo.PhotoProvider;
import success.planfit.utils.TestUtil;
import success.planfit.utils.UserInfo;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookmarkServiceTest {

    private static final String BASIC_GOOGLE_PLACES_IDENTIFIER = "helloGoogleIdentifier";
    private static final String BASIC_SPACE_NAME = "helloSpaceName";
    private static final String BASIC_LOCATION = "helloLocation";
    private static final SpaceType BASIC_SPACE_TYPE = SpaceType.TYPE;
    private static final String BASIC_LINK = "https://hello.com";
    private static final Double BASIC_LATITUDE = 1.23456789;
    private static final Double BASIC_LONGITUDE = 12.3456789;
    private static String BASIC_SPACE_PHOTO_STRING;
    private static byte[] BASIC_SPACE_PHOTO_BYTES;

    @Autowired
    private EntityManager em;
    @Autowired
    private BookmarkService bookmarkService;
    @Autowired
    private TestUtil util;

    @BeforeEach
    void beforeEach() {
        BASIC_SPACE_PHOTO_STRING = util.getEncodedImage();
        BASIC_SPACE_PHOTO_BYTES = util.getBinaryImage();
    }

    @AfterEach
    void afterEach() {
        util.clearEntityManager(em);
    }

    @Nested
    @DisplayName("장소 즐겨찾기")
    class SpaceBookmarkTests {

        @Test
        @DisplayName("등록 성공")
        void registerSuccess() throws Exception {
            // given: 회원 및 Request DTO 생성
            UserInfo userInfo = util.signUpPlanfitUser();
            SpaceBookmarkRegistrationRequestDto requestDto = getBasicSpaceBookmarkRegistrationRequestDto();
            util.clearEntityManager(em);

            // when: 장소 즐겨찾기 등록
            bookmarkService.registerSpaceBookmark(userInfo.userId(), requestDto);
            util.clearEntityManager(em);

            // then: 장소 즐겨찾기가 의도대로 생성되었는지 검증
            List<SpaceBookmark> spaceBookmarks = em.find(User.class, userInfo.userId())
                    .getSpaceBookmarks();
            assertThat(spaceBookmarks.size()).isEqualTo(1);
            SpaceBookmark spaceBookmark = spaceBookmarks.getFirst();
            assertThat(isBasicSpaceBookmark(spaceBookmark)).isTrue();
        }

        @Test
        @DisplayName("장소 즐겨찾기가 하나일 때 전체 조회 성공")
        void findAllWhenOneSuccess() {
            // given: 회원 및 장소 즐겨찾기 생성

            // when: 장소 즐겨찾기 조회

            // then: 제대로 조회되었는지 검증
        }

        @Test
        @DisplayName("장소 즐겨찾기가 여러개일 때 전체 조회 성공")
        void findAllSuccess() {
            // given: 회원 및 장소 즐겨찾기 생성
            /** 서비스에 의존해 엔티티를 생성하지 말고 EntityManager를 통해 생성하도록 로직 수정해야 함 **/

            // when: 장소 즐겨찾기 조회

            // then: 제대로 조회되었는지 검증
        }

        @Test
        @DisplayName("장소 즐겨찾기가 하나일 때 삭제 성공")
        void removeSuccess() {
            // given: 회원 및 장소 즐겨찾기 생성 & 조회

            // when: 장소 즐겨찾기 삭제

            // then: 장소 즐겨찾기가 삭제된 것 확인
        }

        @Test
        @DisplayName("장소 즐겨찾기가 여러개일 때 원하는 것만 삭제 성공")
        void removeCorrectOneAmongSpaceBookmarksSuccess() {
            // given: 회원 및 장소 즐겨찾기 생성 & 조회

            // when: 특정 장소 즐겨찾기만 삭제

            // then: 의도된 엔티티만 삭제되었는지 검증
        }
    }

    private SpaceBookmarkRegistrationRequestDto getBasicSpaceBookmarkRegistrationRequestDto() {
        return SpaceBookmarkRegistrationRequestDto.builder()
                .googlePlacesIdentifier(BASIC_GOOGLE_PLACES_IDENTIFIER)
                .spaceName(BASIC_SPACE_NAME)
                .location(BASIC_LOCATION)
                .spaceType(BASIC_SPACE_TYPE)
                .link(BASIC_LINK)
                .latitude(BASIC_LATITUDE)
                .longitude(BASIC_LONGITUDE)
                .spacePhoto(BASIC_SPACE_PHOTO_STRING)
                .build();
    }

    private SpaceBookmark createSpaceBookmark(String googlePlacesIdentifier) {
        return SpaceBookmark.builder()
                .googlePlacesIdentifier(googlePlacesIdentifier)
                .spaceInformation(SpaceInformation.builder()
                        .spaceName(BASIC_SPACE_NAME)
                        .location(BASIC_LOCATION)
                        .spaceTag(BASIC_SPACE_TYPE)
                        .link(BASIC_LINK)
                        .latitude(BASIC_LATITUDE)
                        .longitude(BASIC_LONGITUDE)
                        .spacePhoto(BASIC_SPACE_PHOTO_BYTES)
                        .build()
                )
                .build();
    }

    private boolean isBasicSpaceBookmark(SpaceBookmark spaceBookmark) {
        SpaceInformation spaceInformation = spaceBookmark.getSpaceInformation();
        return spaceBookmark.getGooglePlacesIdentifier().equals(BASIC_GOOGLE_PLACES_IDENTIFIER) ||
                spaceInformation.getSpaceName().equals(BASIC_SPACE_NAME) ||
                spaceInformation.getLocation().equals(BASIC_LOCATION) ||
                spaceInformation.getSpaceTag().equals(BASIC_SPACE_TYPE) ||
                spaceInformation.getLink().equals(BASIC_LINK) ||
                spaceInformation.getLatitude().equals(BASIC_LATITUDE) ||
                spaceInformation.getLongitude().equals(BASIC_LONGITUDE) ||
                Arrays.equals(spaceInformation.getSpacePhoto(), BASIC_SPACE_PHOTO_BYTES);
    }

    private boolean isBasicSpaceBookmarkInfoResponseDto(SpaceBookmarkInfoResponseDto responseDto) {
        return responseDto.googlePlacesIdentifier().equals(BASIC_GOOGLE_PLACES_IDENTIFIER) ||
                responseDto.spaceName().equals(BASIC_SPACE_NAME) ||
                responseDto.location().equals(BASIC_LOCATION) ||
                responseDto.spaceType().equals(BASIC_SPACE_TYPE) ||
                responseDto.link().equals(BASIC_LINK) ||
                responseDto.latitude().equals(BASIC_LATITUDE) ||
                responseDto.longitude().equals(BASIC_LONGITUDE) ||
                responseDto.spacePhoto().equals(BASIC_SPACE_PHOTO_STRING);
    }

    private boolean areEqualEntityAndDto(SpaceBookmark entity, SpaceBookmarkInfoResponseDto dto) {
        SpaceInformation spaceInformation = entity.getSpaceInformation();
        return entity.getGooglePlacesIdentifier().equals(dto.googlePlacesIdentifier()) ||
                spaceInformation.getSpaceName().equals(dto.spaceName()) ||
                spaceInformation.getLocation().equals(dto.location()) ||
                spaceInformation.getSpaceTag().equals(dto.spaceType()) ||
                spaceInformation.getLink().equals(dto.link()) ||
                spaceInformation.getLatitude().equals(dto.latitude()) ||
                spaceInformation.getLongitude().equals(dto.longitude());
    }

}