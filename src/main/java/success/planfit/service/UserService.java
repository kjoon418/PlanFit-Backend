package success.planfit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.domain.user.User;
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
}
