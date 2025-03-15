package success.planfit.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.entity.user.User;
import success.planfit.global.exception.EntityNotFoundException;
import success.planfit.repository.UserRepository;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class PostService {


    private final UserRepository userRepository;

    public void registerPost(Long userId){
        User user = userRepository.findByIdWithSpaceBookmark(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 조회 실패"));


    }


}
