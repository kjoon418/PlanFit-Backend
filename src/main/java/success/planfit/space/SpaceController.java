package success.planfit.space;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import success.planfit.course.dto.SpaceResponseDto;
import success.planfit.global.controller.ControllerUtil;
import success.planfit.space.dto.request.SpaceDetailRequestDto;
import success.planfit.space.dto.request.SpaceRequestFromAI;
import success.planfit.space.dto.response.SpaceInfoForAIDto;
import success.planfit.space.service.SpaceService;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/space")
public class SpaceController {

    private final ControllerUtil controllerUtil;
    private final SpaceService spaceService;

    /**
     * AI에게 장소 조회 요청
     */
    public ResponseEntity<SpaceInfoForAIDto> requestToAI(Principal principal, @RequestBody SpaceDetailRequestDto requestDto){
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        SpaceInfoForAIDto spaceInfoForAIDto = spaceService.requestToAI(userId, requestDto);
        return ResponseEntity.ok(spaceInfoForAIDto);
    }

    /**
     * AI에게 장소 받아서 갱신, 정렬 후, 프론트 장소 리스트에게 전달
     */
    public ResponseEntity<List<SpaceResponseDto>> requestToAI(Principal principal, @RequestBody List<SpaceRequestFromAI> requestDtoList){
        Long userId = controllerUtil.findUserIdByPrincipal(principal);
        List<SpaceResponseDto> spaceResponseDtoList = spaceService.responseToFE(requestDtoList);
        return ResponseEntity.ok(spaceResponseDtoList);
    }
}
