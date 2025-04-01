package success.planfit.user.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.entity.user.PlanfitUser;
import success.planfit.entity.user.User;
import success.planfit.global.jwt.TokenProvider;
import success.planfit.global.jwt.TokenType;
import success.planfit.global.jwt.dto.TokenResponseDto;
import success.planfit.repository.UserRepository;
import success.planfit.user.dto.PlanfitUserSignInRequestDto;
import success.planfit.user.dto.PlanfitUserSignUpRequestDto;

import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class PlanfitAuthorizationService {

    private static final Supplier<EntityNotFoundException> USER_NOT_FOUND_EXCEPTION_BY_ID_AND_PASSWORD = () -> new EntityNotFoundException("해당 아이디와 비밀번호를 가진 회원을 찾을 수 없습니다");

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public TokenResponseDto planfitSignUp(PlanfitUserSignUpRequestDto requestDto) {
        PlanfitUser user = requestDto.toEntity();
        userRepository.save(user);

        String accessTokenValue = tokenProvider.createToken(user, TokenType.ACCESS);
        String refreshTokenValue = tokenProvider.createToken(user, TokenType.REFRESH);

        setRefreshToken(user, refreshTokenValue);

        return createTokenResponseDto(accessTokenValue, refreshTokenValue);
    }

    @Transactional
    public TokenResponseDto planfitSignIn(PlanfitUserSignInRequestDto requestDto) {
        User user = findUserBySignInRequestDto(requestDto);

        String accessTokenValue = tokenProvider.createToken(user, TokenType.ACCESS);
        String refreshTokenValue = tokenProvider.createToken(user, TokenType.REFRESH);

        setRefreshToken(user, refreshTokenValue);

        return createTokenResponseDto(accessTokenValue, refreshTokenValue);
    }

    @Transactional(readOnly = true)
    public boolean isDuplicatedLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .isPresent();
    }

    private void setRefreshToken(User user, String refreshTokenValue) {
        user.getRefreshToken()
                .setTokenValue(refreshTokenValue);
    }

    private User findUserBySignInRequestDto(PlanfitUserSignInRequestDto requestDto) {
        return userRepository.findByLoginIdAndPassword(requestDto.getLoginId(), requestDto.getPassword())
                .orElseThrow(USER_NOT_FOUND_EXCEPTION_BY_ID_AND_PASSWORD);
    }

    private TokenResponseDto createTokenResponseDto(String accessToken, String refreshToken) {
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
