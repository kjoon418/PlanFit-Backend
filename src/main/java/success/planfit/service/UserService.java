package success.planfit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.domain.user.PlanfitUser;
import success.planfit.domain.user.User;
import success.planfit.domain.user.UserUpdateDto;
import success.planfit.photo.PhotoProvider;
import success.planfit.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findById(Long id) {
        log.info("UserService.findById() called");

        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id로 회원이 조회되지 않음."));
    }
    // 회원 정보 조회 (기본키로 조회)
    public UserUpdateDto getUserInfo(Long userId) {  // 회원 기본키 (userId)를 파라미터로 받음

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보 조회 실패"));

        return UserUpdateDto.from(user);  // UserDto로 변환하여 반환
    }

    // 회원 정보 수정
    @Transactional
    public void updateUserInfo(Long userId, UserUpdateDto userDto) {
        User user = userRepository.findById(userId)  // ID로 조회
                .orElseThrow(() -> new RuntimeException("회원 정보 조회 실패"));

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
        if (userDto.getPassword() != null && user instanceof PlanfitUser) {
            PlanfitUser planfitUser = (PlanfitUser) user;
            planfitUser.setPassword(userDto.getPassword());
        }

        userRepository.save(user);
    }
}
