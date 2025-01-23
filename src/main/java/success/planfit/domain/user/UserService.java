package success.planfit.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 회원 정보 조회 (기본키로 조회)
    public UserDto getUserInfo(Long userId) {  // 회원 기본키 (userId)를 파라미터로 받음
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보 조회 실패"));

        return UserDto.from(user);  // UserDto로 변환하여 반환
    }

    // 회원 정보 수정
    @Transactional
    public void updateUserInfo(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)  // ID로 조회
                .orElseThrow(() -> new RuntimeException("회원 정보 조회 실패"));

        if (userDto.getProfilePhoto() != null) {
            user.setProfilePhoto(userDto.getProfilePhoto());
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

        userRepository.save(user);
    }
}
