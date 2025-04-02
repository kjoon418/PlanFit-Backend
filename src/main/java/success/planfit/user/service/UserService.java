package success.planfit.user.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.entity.user.PlanfitUser;
import success.planfit.entity.user.User;
import success.planfit.global.exception.IllegalRequestException;
import success.planfit.global.jwt.TokenProvider;
import success.planfit.global.jwt.TokenType;
import success.planfit.global.jwt.dto.AccessTokenResponseDto;
import success.planfit.global.photo.PhotoProvider;
import success.planfit.repository.UserRepository;
import success.planfit.user.dto.UserUpdateDto;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Supplier<EntityNotFoundException> USER_NOT_FOUND_EXCEPTION = () -> new EntityNotFoundException("해당 ID로 회원을 조회할 수 없습니다.");
    private static final Supplier<EntityNotFoundException> USER_NOT_FOUND_EXCEPTION_BY_TOKEN = () -> new EntityNotFoundException("해당 JWT 토큰으로 회원을 조회할 수 없습니다.");

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Transactional(readOnly = true)
    public UserUpdateDto getUserInfo(long userId) {
        User user = findUserById(userId);

        return UserUpdateDto.from(user);
    }

    @Transactional
    public void updateUserInfo(long userId, UserUpdateDto userDto) {
        User user = findUserById(userId);

        if (userDto.getProfilePhoto() != null) {
            user.setProfilePhoto(PhotoProvider.decode(userDto.getProfilePhoto()));
        }
        if (userDto.getBirthOfDate() != null) {
            user.setBirthOfDate(userDto.getBirthOfDate());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getPhoneNumber() != null) {
            user.setPhoneNumber(userDto.getPhoneNumber());
        }
        if (userDto.getIdentity() != null) {
            user.setIdentity(userDto.getIdentity());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null && user instanceof PlanfitUser planfitUser) {
            planfitUser.setPassword(userDto.getPassword());
        }

        userRepository.save(user);
    }

    @Transactional
    public void invalidateRefreshToken(long userId) {
        User user = findUserById(userId);
        user.getRefreshToken().setTokenValue(null);
    }

    @Transactional
    public void deleteUser(long userId) {
        User user = findUserById(userId);

        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public AccessTokenResponseDto reissueAccessToken(String refreshToken) {
        long userId = Long.parseLong(tokenProvider.parseClaims(refreshToken).getSubject());
        User user = userRepository.findById(userId)
                .orElseThrow(USER_NOT_FOUND_EXCEPTION_BY_TOKEN);

        if (!tokenProvider.validateToken(refreshToken, TokenType.REFRESH)) {
            throw new IllegalRequestException("부적절한 리프레쉬 토큰입니다.");
        }

        if (!isEqualWithSavedToken(user, refreshToken)) {
            throw new IllegalRequestException("만료된 리프레쉬 토큰입니다.");
        }

        return AccessTokenResponseDto.builder()
                .accessToken(tokenProvider.createToken(user, TokenType.ACCESS))
                .build();
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(USER_NOT_FOUND_EXCEPTION);
    }

    private boolean isEqualWithSavedToken(User user, String refreshTokenValue) {
        String actualValue = user.getRefreshToken()
                .getTokenValue();

        return refreshTokenValue.equals(actualValue);
    }

}
